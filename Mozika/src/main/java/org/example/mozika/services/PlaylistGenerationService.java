package org.example.mozika.services;

import org.example.mozika.dto.playlist.PlaylistGenerationProposalResponse;
import org.example.mozika.dto.playlist.PlaylistGenerationRequest;
import org.example.mozika.dto.playlist.PlaylistGenerationSongResponse;
import org.example.mozika.dto.playlist.SaveGeneratedPlaylistRequest;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.*;
import org.example.mozika.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class PlaylistGenerationService {
	private final PlaylistRepository playlistRepository;
	private final PlaylistSongRepository playlistSongRepository;
	private final PlaylistGenerationCriteriaRepository criteriaRepository;
	private final PlaylistVisibilitieRepository visibilityRepository;
	private final UserRepository userRepository;
	private final SongRepository songRepository;
	private final SongGenreRepository songGenreRepository;
	private final GenreRepository genreRepository;
	private final ArtistRepository artistRepository;

	public PlaylistGenerationService(
			PlaylistRepository playlistRepository,
			PlaylistSongRepository playlistSongRepository,
			PlaylistGenerationCriteriaRepository criteriaRepository,
			PlaylistVisibilitieRepository visibilityRepository,
			UserRepository userRepository,
			SongRepository songRepository,
			SongGenreRepository songGenreRepository,
			GenreRepository genreRepository,
			ArtistRepository artistRepository) {
		this.playlistRepository = playlistRepository;
		this.playlistSongRepository = playlistSongRepository;
		this.criteriaRepository = criteriaRepository;
		this.visibilityRepository = visibilityRepository;
		this.userRepository = userRepository;
		this.songRepository = songRepository;
		this.songGenreRepository = songGenreRepository;
		this.genreRepository = genreRepository;
		this.artistRepository = artistRepository;
	}

	@Transactional(readOnly = true)
	public List<PlaylistGenerationProposalResponse> generate(PlaylistGenerationRequest request) {
		ValidatedCriteria criteria = validateCriteria(request);
		Map<Long, Set<Long>> genresBySong = loadGenresBySong();
		List<Song> eligibleSongs = songRepository.findAll().stream()
				.filter(song -> matches(song, genresBySong, criteria))
				.toList();

		List<List<Song>> proposalSongs = new ArrayList<>();
		List<PlaylistGenerationProposalResponse> proposals = new ArrayList<>();
		int targetSeconds = request.getDurationMinutes() * 60;
		for (int attempt = 0; attempt < 30 && proposals.size() < 3; attempt++) {
			List<Song> proposal = buildProposal(eligibleSongs, targetSeconds, proposalSongs);
			if (proposal.isEmpty() || proposalSongs.stream().anyMatch(existing -> sameSongs(existing, proposal))) {
				continue;
			}
			proposalSongs.add(proposal);
			int duration = proposal.stream().mapToInt(Song::getDurationSeconds).sum();
			proposals.add(new PlaylistGenerationProposalResponse(
					proposals.size() + 1,
					duration,
					proposal.stream().map(this::toSongResponse).toList()));
		}
		return proposals;
	}

	@Transactional
	public Playlist save(String email, SaveGeneratedPlaylistRequest request) {
		if (request.getCriteria() == null) {
			throw new IllegalArgumentException("Les critères de génération sont obligatoires");
		}
		ValidatedCriteria criteria = validateCriteria(request.getCriteria());
		if (request.getSongIds() == null || request.getSongIds().isEmpty()
				|| request.getSongIds().stream().anyMatch(Objects::isNull)
				|| new HashSet<>(request.getSongIds()).size() != request.getSongIds().size()) {
			throw new IllegalArgumentException("La proposition doit contenir des titres distincts");
		}

		Map<Long, Set<Long>> genresBySong = loadGenresBySong();
		Map<Long, Song> songsById = songRepository.findAllById(request.getSongIds()).stream()
				.collect(Collectors.toMap(Song::getId, song -> song));
		if (songsById.size() != request.getSongIds().size()) {
			throw new ResourceNotFoundException("Une ou plusieurs chansons proposées sont introuvables");
		}
		List<Song> songs = request.getSongIds().stream().map(songsById::get).toList();
		if (songs.stream().anyMatch(song -> !matches(song, genresBySong, criteria))) {
			throw new IllegalArgumentException("La proposition ne respecte pas les critères sélectionnés");
		}
		int totalDuration = songs.stream().mapToInt(Song::getDurationSeconds).sum();
		if (totalDuration > request.getCriteria().getDurationMinutes() * 60) {
			throw new IllegalArgumentException("La durée de la proposition dépasse la durée cible");
		}

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
		PlaylistVisibilitie visibility = visibilityRepository.findByName("PRIVATE")
				.orElseGet(() -> visibilityRepository.findAll().stream().findFirst()
						.orElseThrow(() -> new ResourceNotFoundException("No playlist visibility found")));
		LocalDateTime now = LocalDateTime.now();
		Playlist playlist = new Playlist();
		playlist.setName(request.getName().trim());
		playlist.setOwneruseridUsers(user);
		playlist.setVisibilityidPlaylistVisibilities(visibility);
		playlist.setCreatedAt(now);
		playlist.setUpdatedAt(now);
		Playlist savedPlaylist = playlistRepository.save(playlist);

		int position = 1;
		for (Song song : songs) {
			PlaylistSong playlistSong = new PlaylistSong();
			playlistSong.setPlaylistidPlaylists(savedPlaylist);
			playlistSong.setSongidSongs(song);
			playlistSong.setPosition(position++);
			playlistSong.setAddedAt(now);
			playlistSongRepository.save(playlistSong);
		}

		PlaylistGenerationCriteria savedCriteria = new PlaylistGenerationCriteria();
		savedCriteria.setPlaylistidPlaylists(savedPlaylist);
		savedCriteria.setTargetDurationSeconds(request.getCriteria().getDurationMinutes() * 60);
		savedCriteria.setIncludedGenres(criteria.includedGenreNames().toArray(String[]::new));
		savedCriteria.setExcludedGenres(criteria.excludedGenreNames().toArray(String[]::new));
		savedCriteria.setIncludedArtistIds(criteria.includedArtistIds().toArray(Long[]::new));
		savedCriteria.setExcludedArtistIds(criteria.excludedArtistIds().toArray(Long[]::new));
		savedCriteria.setCreatedAt(now);
		criteriaRepository.save(savedCriteria);
		return savedPlaylist;
	}

	private ValidatedCriteria validateCriteria(PlaylistGenerationRequest request) {
		if (request == null || request.getDurationMinutes() == null
				|| request.getDurationMinutes() < 1 || request.getDurationMinutes() > 180) {
			throw new IllegalArgumentException("La durée doit être comprise entre 1 et 180 minutes");
		}
		List<Long> includedGenreIds = normalizedIds(request.getIncludedGenreIds());
		List<Long> excludedGenreIds = normalizedIds(request.getExcludedGenreIds());
		List<Long> includedArtistIds = normalizedIds(request.getIncludedArtistIds());
		List<Long> excludedArtistIds = normalizedIds(request.getExcludedArtistIds());
		if (intersects(includedGenreIds, excludedGenreIds) || intersects(includedArtistIds, excludedArtistIds)) {
			throw new IllegalArgumentException("Un critère ne peut pas être inclus et exclu simultanément");
		}

		Map<Long, Genre> genres = genreRepository.findAllById(
				union(includedGenreIds, excludedGenreIds)).stream()
				.collect(Collectors.toMap(Genre::getId, genre -> genre));
		Map<Long, Artist> artists = artistRepository.findAllById(
				union(includedArtistIds, excludedArtistIds)).stream()
				.collect(Collectors.toMap(Artist::getId, artist -> artist));
		if (genres.size() != union(includedGenreIds, excludedGenreIds).size()
				|| artists.size() != union(includedArtistIds, excludedArtistIds).size()) {
			throw new ResourceNotFoundException("Un genre ou artiste sélectionné est introuvable");
		}

		return new ValidatedCriteria(
				Set.copyOf(includedGenreIds),
				Set.copyOf(excludedGenreIds),
				Set.copyOf(includedArtistIds),
				Set.copyOf(excludedArtistIds),
				includedGenreIds.stream().map(id -> genres.get(id).getName()).toList(),
				excludedGenreIds.stream().map(id -> genres.get(id).getName()).toList());
	}

	private Map<Long, Set<Long>> loadGenresBySong() {
		Map<Long, Set<Long>> result = new HashMap<>();
		for (SongGenre songGenre : songGenreRepository.findAll()) {
			if (songGenre.getSongidSongs() != null && songGenre.getGenreidGenres() != null) {
				result.computeIfAbsent(songGenre.getSongidSongs().getId(), ignored -> new HashSet<>())
						.add(songGenre.getGenreidGenres().getId());
			}
		}
		return result;
	}

	private boolean matches(Song song, Map<Long, Set<Long>> genresBySong, ValidatedCriteria criteria) {
		if (song.getDurationSeconds() == null || song.getDurationSeconds() <= 0) {
			return false;
		}
		Long artistId = song.getArtistidArtists() == null ? null : song.getArtistidArtists().getId();
		Set<Long> songGenres = genresBySong.getOrDefault(song.getId(), Set.of());
		return (criteria.includedGenreIds().isEmpty()
					|| criteria.includedGenreIds().stream().anyMatch(songGenres::contains))
				&& (criteria.includedArtistIds().isEmpty()
					|| (artistId != null && criteria.includedArtistIds().contains(artistId)))
				&& criteria.excludedGenreIds().stream().noneMatch(songGenres::contains)
				&& (artistId == null || !criteria.excludedArtistIds().contains(artistId));
	}

	private List<Song> buildProposal(List<Song> eligibleSongs, int targetSeconds, List<List<Song>> previous) {
		List<Song> remaining = new ArrayList<>(eligibleSongs);
		List<Song> chosen = new ArrayList<>();
		int secondsLeft = targetSeconds;
		while (secondsLeft > 0) {
			final int remainingSeconds = secondsLeft;
			List<Song> fitting = remaining.stream()
					.filter(song -> song.getDurationSeconds() <= remainingSeconds)
					.toList();
			if (fitting.isEmpty()) {
				break;
			}
			int leastReuse = fitting.stream().mapToInt(song -> reuseCount(song, previous)).min().orElse(0);
			List<Song> lessReused = fitting.stream()
					.filter(song -> reuseCount(song, previous) == leastReuse)
					.toList();
			int largestFit = lessReused.stream().mapToInt(Song::getDurationSeconds).max().orElseThrow();
			int minimumNearFit = Math.max(1, (int) Math.floor(largestFit * 0.85));
			List<Song> nearFit = lessReused.stream()
					.filter(song -> song.getDurationSeconds() >= minimumNearFit)
					.toList();
			Song selected = nearFit.get(ThreadLocalRandom.current().nextInt(nearFit.size()));
			chosen.add(selected);
			remaining.remove(selected);
			secondsLeft -= selected.getDurationSeconds();
		}
		return chosen;
	}

	private int reuseCount(Song song, List<List<Song>> previous) {
		return (int) previous.stream().filter(list -> list.stream()
				.anyMatch(previousSong -> previousSong.getId().equals(song.getId()))).count();
	}

	private boolean sameSongs(List<Song> first, List<Song> second) {
		return first.stream().map(Song::getId).collect(Collectors.toSet())
				.equals(second.stream().map(Song::getId).collect(Collectors.toSet()));
	}

	private PlaylistGenerationSongResponse toSongResponse(Song song) {
		return new PlaylistGenerationSongResponse(
				song.getId(),
				song.getTitle(),
				song.getArtistidArtists() == null ? "" : song.getArtistidArtists().getStageName(),
				song.getDurationSeconds(),
				song.getAlbumidAlbums() == null ? null : song.getAlbumidAlbums().getCoverUrl());
	}

	private List<Long> normalizedIds(List<Long> ids) {
		if (ids == null) {
			return List.of();
		}
		if (ids.stream().anyMatch(Objects::isNull) || new HashSet<>(ids).size() != ids.size()) {
			throw new IllegalArgumentException("Les critères ne peuvent pas contenir d'identifiants nuls ou répétés");
		}
		return ids;
	}

	private boolean intersects(List<Long> left, List<Long> right) {
		return left.stream().anyMatch(new HashSet<>(right)::contains);
	}

	private List<Long> union(List<Long> first, List<Long> second) {
		Set<Long> result = new LinkedHashSet<>(first);
		result.addAll(second);
		return new ArrayList<>(result);
	}

	private record ValidatedCriteria(
			Set<Long> includedGenreIds,
			Set<Long> excludedGenreIds,
			Set<Long> includedArtistIds,
			Set<Long> excludedArtistIds,
			List<String> includedGenreNames,
			List<String> excludedGenreNames) {
	}
}
