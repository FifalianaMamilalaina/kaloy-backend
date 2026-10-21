package org.example.mozika.services;

import org.example.mozika.dto.playlist.AddSongsResult;
import org.example.mozika.dto.playlist.AddSongsToPlaylistRequest;
import org.example.mozika.dto.playlist.CreatePlaylistMobileRequest;
import org.example.mozika.dto.playlist.MergePlaylistsMobileRequest;
import org.example.mozika.dto.playlist.PlaylistSongMobileResponse;
import org.example.mozika.dto.playlist.PlaylistUiItemResponse;
import org.example.mozika.dto.playlist.ReorderSongItem;
import org.example.mozika.dto.playlist.UpdatePlaylistMobileRequest;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.*;
import org.example.mozika.repositories.*;
import org.example.mozika.services.interfaces.PlaylistMobileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DefaultPlaylistMobileService implements PlaylistMobileService {

	private final PlaylistRepository playlistRepository;
	private final PlaylistSongRepository playlistSongRepository;
	private final PlaylistDownloadRepository playlistDownloadRepository;
	private final PlaylistFusionRepository playlistFusionRepository;
	private final PlaylistVisibilitieRepository playlistVisibilitieRepository;
	private final EditorialPlaylistRepository editorialPlaylistRepository;
	private final UserRepository userRepository;
	private final SongRepository songRepository;

	public DefaultPlaylistMobileService(
			PlaylistRepository playlistRepository,
			PlaylistSongRepository playlistSongRepository,
			PlaylistDownloadRepository playlistDownloadRepository,
			PlaylistFusionRepository playlistFusionRepository,
			PlaylistVisibilitieRepository playlistVisibilitieRepository,
			EditorialPlaylistRepository editorialPlaylistRepository,
			UserRepository userRepository,
			SongRepository songRepository) {
		this.playlistRepository = playlistRepository;
		this.playlistSongRepository = playlistSongRepository;
		this.playlistDownloadRepository = playlistDownloadRepository;
		this.playlistFusionRepository = playlistFusionRepository;
		this.playlistVisibilitieRepository = playlistVisibilitieRepository;
		this.editorialPlaylistRepository = editorialPlaylistRepository;
		this.userRepository = userRepository;
		this.songRepository = songRepository;
	}

	@Override
	public List<PlaylistUiItemResponse> getMesPlaylists(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

		List<Playlist> playlists = playlistRepository.findByOwneruseridUsers(user);
		List<PlaylistUiItemResponse> result = new ArrayList<>();

		for (Playlist playlist : playlists) {
			List<PlaylistSong> songs = playlistSongRepository.findByPlaylistidPlaylists(playlist);
			int nombreTitres = songs.size();
			int dureeSecondes = songs.stream()
					.mapToInt(ps -> ps.getSongidSongs().getDurationSeconds() != null
							? ps.getSongidSongs().getDurationSeconds() : 0)
					.sum();
			boolean estTelecharge = playlistDownloadRepository
					.findByUseridUsersAndPlaylistidPlaylists(user, playlist)
					.map(PlaylistDownload::getIsActive)
					.orElse(false);

			result.add(new PlaylistUiItemResponse(
					playlist.getId(),
					playlist.getName(),
					null,
					nombreTitres,
					dureeSecondes,
					estTelecharge,
					playlist.getShareToken(),
					playlist.getUpdatedAt() != null ? playlist.getUpdatedAt().toString() : null
			));
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public PlaylistUiItemResponse getPlaylist(String email, Long playlistId) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

		Playlist playlist = playlistRepository.findById(playlistId)
				.orElseThrow(() -> new ResourceNotFoundException("Playlist not found: " + playlistId));

		if (!playlist.getOwneruseridUsers().getEmail().equals(email)) {
			throw new ResourceNotFoundException("Playlist not found: " + playlistId);
		}

		List<PlaylistSong> songs = playlistSongRepository.findByPlaylistidPlaylists(playlist);
		int nombreTitres = songs.size();
		int dureeSecondes = songs.stream()
				.mapToInt(ps -> ps.getSongidSongs().getDurationSeconds() != null
						? ps.getSongidSongs().getDurationSeconds() : 0)
				.sum();
		boolean estTelecharge = playlistDownloadRepository
				.findByUseridUsersAndPlaylistidPlaylists(user, playlist)
				.map(PlaylistDownload::getIsActive)
				.orElse(false);

		return new PlaylistUiItemResponse(
				playlist.getId(),
				playlist.getName(),
				null,
				nombreTitres,
				dureeSecondes,
				estTelecharge,
				playlist.getShareToken(),
				playlist.getUpdatedAt() != null ? playlist.getUpdatedAt().toString() : null
		);
	}

	@Override
	@Transactional
	public Playlist updatePlaylist(String email, Long playlistId, UpdatePlaylistMobileRequest request) {
		Playlist playlist = playlistRepository.findById(playlistId)
				.orElseThrow(() -> new ResourceNotFoundException("Playlist not found: " + playlistId));

		if (!playlist.getOwneruseridUsers().getEmail().equals(email)) {
			throw new ResourceNotFoundException("Playlist not found: " + playlistId);
		}

		playlist.setName(request.getName().trim());
		playlist.setDescription(request.getDescription());
		playlist.setUpdatedAt(LocalDateTime.now());

		return playlistRepository.save(playlist);
	}

	@Override
	public List<EditorialPlaylist> getFeaturedEditorials() {
		return editorialPlaylistRepository.findByIsFeaturedTrue();
	}

	@Override
	@Transactional
	public Playlist createPlaylist(String email, CreatePlaylistMobileRequest request) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

		PlaylistVisibilitie visibility = playlistVisibilitieRepository.findByName("PRIVATE")
				.orElseGet(() -> playlistVisibilitieRepository.findAll().stream()
						.findFirst()
						.orElseThrow(() -> new ResourceNotFoundException("No playlist visibility found")));

		Playlist playlist = new Playlist();
		playlist.setName(request.getName());
		playlist.setOwneruseridUsers(user);
		playlist.setVisibilityidPlaylistVisibilities(visibility);
		playlist.setCreatedAt(LocalDateTime.now());
		playlist.setUpdatedAt(LocalDateTime.now());

		return playlistRepository.save(playlist);
	}

	@Override
	@Transactional
	public void deletePlaylist(String email, Long playlistId) {
		Playlist playlist = playlistRepository.findById(playlistId)
				.orElseThrow(() -> new ResourceNotFoundException("Playlist not found: " + playlistId));

		if (!playlist.getOwneruseridUsers().getEmail().equals(email)) {
			throw new ResourceNotFoundException("Playlist not found: " + playlistId);
		}

		List<PlaylistSong> songs = playlistSongRepository.findByPlaylistidPlaylists(playlist);
		playlistSongRepository.deleteAll(songs);

		User user = playlist.getOwneruseridUsers();
		playlistDownloadRepository.findByUseridUsersAndPlaylistidPlaylists(user, playlist)
				.ifPresent(playlistDownloadRepository::delete);

		playlistRepository.delete(playlist);
	}

	@Override
	@Transactional
	public Playlist mergePlaylist(String email, MergePlaylistsMobileRequest request) {
		if (request.getSourceIds() == null || request.getSourceIds().size() < 2) {
			throw new IllegalArgumentException("Au moins 2 playlists sont nécessaires pour fusionner");
		}
		if (request.getSourceIds().stream().anyMatch(Objects::isNull)) {
			throw new IllegalArgumentException("Les identifiants des playlists sources sont obligatoires");
		}
		if (new HashSet<>(request.getSourceIds()).size() != request.getSourceIds().size()) {
			throw new IllegalArgumentException("Une playlist source ne peut être sélectionnée qu'une seule fois");
		}
		if (request.getResultName() == null || request.getResultName().isBlank()) {
			throw new IllegalArgumentException("Le nom de la playlist résultante est obligatoire");
		}
		String resultName = request.getResultName().trim();
		if (resultName.length() > 150) {
			throw new IllegalArgumentException("Le nom de la playlist résultante ne peut pas dépasser 150 caractères");
		}

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

		List<Playlist> sources = new ArrayList<>();
		for (Long sourceId : request.getSourceIds()) {
			Playlist source = playlistRepository.findById(sourceId)
					.orElseThrow(() -> new ResourceNotFoundException("Playlist not found: " + sourceId));
			if (!source.getOwneruseridUsers().getEmail().equals(email)) {
				throw new ResourceNotFoundException("Playlist not found: " + sourceId);
			}
			sources.add(source);
		}

		PlaylistVisibilitie visibility = playlistVisibilitieRepository.findByName("PRIVATE")
				.orElseGet(() -> playlistVisibilitieRepository.findAll().stream()
						.findFirst()
						.orElseThrow(() -> new ResourceNotFoundException("No playlist visibility found")));

		Playlist result = new Playlist();
		result.setName(resultName);
		result.setOwneruseridUsers(user);
		result.setVisibilityidPlaylistVisibilities(visibility);
		result.setCreatedAt(LocalDateTime.now());
		result.setUpdatedAt(LocalDateTime.now());
		result = playlistRepository.save(result);

		// Collect unique songs maintaining insertion order
		Map<Long, Song> uniqueSongs = new LinkedHashMap<>();
		for (Playlist source : sources) {
			List<PlaylistSong> songs = playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(source);
			for (PlaylistSong ps : songs) {
				uniqueSongs.putIfAbsent(ps.getSongidSongs().getId(), ps.getSongidSongs());
			}
		}

		int position = 1;
		final Playlist savedResult = result;
		for (Song song : uniqueSongs.values()) {
			PlaylistSong newPs = new PlaylistSong();
			newPs.setPlaylistidPlaylists(savedResult);
			newPs.setSongidSongs(song);
			newPs.setPosition(position++);
			newPs.setAddedAt(LocalDateTime.now());
			playlistSongRepository.save(newPs);
		}

		for (Playlist source : sources) {
			PlaylistFusion fusion = new PlaylistFusion();
			fusion.setResultPlaylist(savedResult);
			fusion.setSourcePlaylist(source);
			fusion.setFusedAt(LocalDateTime.now());
			playlistFusionRepository.save(fusion);
		}

		return savedResult;
	}

	@Override
	@Transactional
	public void downloadPlaylist(String email, Long playlistId) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

		Playlist playlist = playlistRepository.findById(playlistId)
				.orElseThrow(() -> new ResourceNotFoundException("Playlist not found: " + playlistId));

		long count = playlistDownloadRepository.countByUseridUsersAndIsActiveTrue(user);
		if (count >= 10) {
			throw new IllegalStateException("Limite de 10 téléchargements atteinte");
		}

		PlaylistDownload download = playlistDownloadRepository
				.findByUseridUsersAndPlaylistidPlaylists(user, playlist)
				.orElse(new PlaylistDownload());

		download.setUseridUsers(user);
		download.setPlaylistidPlaylists(playlist);
		download.setDownloadedAt(LocalDateTime.now());
		download.setExpiresAt(LocalDateTime.now().plusDays(30));
		download.setIsActive(true);

		playlistDownloadRepository.save(download);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PlaylistSongMobileResponse> getSongsInPlaylist(String email, Long playlistId) {
		userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
		Playlist playlist = playlistRepository.findById(playlistId)
				.orElseThrow(() -> new ResourceNotFoundException("Playlist not found: " + playlistId));
		if (!playlist.getOwneruseridUsers().getEmail().equals(email)) {
			throw new ResourceNotFoundException("Playlist not found: " + playlistId);
		}

		List<PlaylistSong> songs = playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(playlist);
		List<PlaylistSongMobileResponse> result = new ArrayList<>();
		for (PlaylistSong ps : songs) {
			Song song = ps.getSongidSongs();
			result.add(new PlaylistSongMobileResponse(
					ps.getId(),
					song.getId(),
					song.getTitle(),
					song.getArtistidArtists() != null ? song.getArtistidArtists().getStageName() : "",
					song.getDurationSeconds(),
					null,
					ps.getPosition(),
					ps.getAddedAt() != null ? ps.getAddedAt().toString() : null
			));
		}
		return result;
	}

	@Override
	@Transactional
	public AddSongsResult addSongsToPlaylist(String email, Long playlistId, AddSongsToPlaylistRequest request) {
		userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
		Playlist playlist = playlistRepository.findById(playlistId)
				.orElseThrow(() -> new ResourceNotFoundException("Playlist not found: " + playlistId));
		if (!playlist.getOwneruseridUsers().getEmail().equals(email)) {
			throw new ResourceNotFoundException("Playlist not found: " + playlistId);
		}

		List<PlaylistSong> existing = playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(playlist);
		Set<Long> existingIds = existing.stream()
				.map(ps -> ps.getSongidSongs().getId())
				.collect(Collectors.toSet());
		int nextPosition = existing.isEmpty() ? 0 : existing.get(existing.size() - 1).getPosition() + 1;

		List<PlaylistSongMobileResponse> added = new ArrayList<>();
		List<Long> alreadyPresent = new ArrayList<>();
		List<Long> notFound = new ArrayList<>();

		for (Long songId : request.getSongIds()) {
			if (existingIds.contains(songId)) {
				alreadyPresent.add(songId);
				continue;
			}
			Optional<Song> songOpt = songRepository.findById(songId);
			if (songOpt.isEmpty()) {
				notFound.add(songId);
				continue;
			}
			Song song = songOpt.get();
			PlaylistSong newPs = new PlaylistSong();
			newPs.setPlaylistidPlaylists(playlist);
			newPs.setSongidSongs(song);
			newPs.setPosition(nextPosition++);
			newPs.setAddedAt(LocalDateTime.now());
			PlaylistSong saved = playlistSongRepository.save(newPs);
			existingIds.add(songId);
			added.add(new PlaylistSongMobileResponse(
					saved.getId(),
					song.getId(),
					song.getTitle(),
					song.getArtistidArtists() != null ? song.getArtistidArtists().getStageName() : "",
					song.getDurationSeconds(),
					null,
					saved.getPosition(),
					saved.getAddedAt().toString()
			));
		}

		playlist.setUpdatedAt(LocalDateTime.now());
		playlistRepository.save(playlist);

		return new AddSongsResult(added, alreadyPresent, notFound);
	}

	@Override
	@Transactional
	public void removeSongFromPlaylist(String email, Long playlistId, Long songId) {
		userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
		Playlist playlist = playlistRepository.findById(playlistId)
				.orElseThrow(() -> new ResourceNotFoundException("Playlist not found: " + playlistId));
		if (!playlist.getOwneruseridUsers().getEmail().equals(email)) {
			throw new ResourceNotFoundException("Playlist not found: " + playlistId);
		}
		Song song = songRepository.findById(songId)
				.orElseThrow(() -> new ResourceNotFoundException("Song not found: " + songId));
		PlaylistSong ps = playlistSongRepository.findByPlaylistidPlaylistsAndSongidSongs(playlist, song)
				.orElseThrow(() -> new ResourceNotFoundException("Song not in playlist"));
		playlistSongRepository.delete(ps);

		List<PlaylistSong> remaining = playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(playlist);
		for (int i = 0; i < remaining.size(); i++) {
			remaining.get(i).setPosition(i);
		}
		playlistSongRepository.saveAll(remaining);

		playlist.setUpdatedAt(LocalDateTime.now());
		playlistRepository.save(playlist);
	}

	@Override
	@Transactional
	public void reorderSongs(String email, Long playlistId, List<ReorderSongItem> items) {
		userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
		Playlist playlist = playlistRepository.findById(playlistId)
				.orElseThrow(() -> new ResourceNotFoundException("Playlist not found: " + playlistId));
		if (!playlist.getOwneruseridUsers().getEmail().equals(email)) {
			throw new ResourceNotFoundException("Playlist not found: " + playlistId);
		}

		List<PlaylistSong> all = playlistSongRepository.findByPlaylistidPlaylists(playlist);
		Map<Long, PlaylistSong> songIdToPs = new HashMap<>();
		for (PlaylistSong ps : all) {
			songIdToPs.put(ps.getSongidSongs().getId(), ps);
		}

		if (items.size() != all.size()) {
			throw new IllegalArgumentException("Reorder list must contain all " + all.size() + " songs in the playlist");
		}

		for (ReorderSongItem item : items) {
			PlaylistSong ps = songIdToPs.get(item.getSongId());
			if (ps == null) {
				throw new ResourceNotFoundException("Song not in playlist: " + item.getSongId());
			}
			ps.setPosition(item.getPosition());
		}
		playlistSongRepository.saveAll(all);

		playlist.setUpdatedAt(LocalDateTime.now());
		playlistRepository.save(playlist);
	}
}
