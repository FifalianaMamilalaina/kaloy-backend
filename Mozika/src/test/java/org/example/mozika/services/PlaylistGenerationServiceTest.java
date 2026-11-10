package org.example.mozika.services;

import org.example.mozika.dto.playlist.PlaylistGenerationRequest;
import org.example.mozika.dto.playlist.SaveGeneratedPlaylistRequest;
import org.example.mozika.models.*;
import org.example.mozika.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistGenerationServiceTest {
	@Mock private PlaylistRepository playlistRepository;
	@Mock private PlaylistSongRepository playlistSongRepository;
	@Mock private PlaylistGenerationCriteriaRepository criteriaRepository;
	@Mock private PlaylistVisibilitieRepository visibilityRepository;
	@Mock private UserRepository userRepository;
	@Mock private SongRepository songRepository;
	@Mock private SongGenreRepository songGenreRepository;
	@Mock private GenreRepository genreRepository;
	@Mock private ArtistRepository artistRepository;

	private PlaylistGenerationService service;
	private User owner;
	private Artist requestedArtist;

	@BeforeEach
	void setUp() {
		service = new PlaylistGenerationService(
				playlistRepository, playlistSongRepository, criteriaRepository,
				visibilityRepository, userRepository, songRepository,
				songGenreRepository, genreRepository, artistRepository);
		owner = new User();
		owner.setEmail("owner@example.com");
		requestedArtist = new Artist();
		requestedArtist.setId(11L);
		lenient().when(artistRepository.findAllById(any())).thenAnswer(invocation ->
				List.of(requestedArtist));
		lenient().when(genreRepository.findAllById(any())).thenReturn(List.of());
		lenient().when(songGenreRepository.findAll()).thenReturn(List.of());
		lenient().when(playlistRepository.save(any(Playlist.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));
		lenient().when(playlistSongRepository.save(any(PlaylistSong.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));
		lenient().when(criteriaRepository.save(any(PlaylistGenerationCriteria.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));
		lenient().when(visibilityRepository.findByName("PRIVATE"))
				.thenReturn(Optional.of(new PlaylistVisibilitie()));
		lenient().when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
	}

	@Test
	void generatesNoMoreThanThreeProposalsWithinDurationAndAppliesArtistFilter() {
		Song first = song(1L, requestedArtist, 70);
		Song second = song(2L, requestedArtist, 50);
		Song otherArtist = song(3L, artist(12L), 30);
		when(artistRepository.findAllById(any())).thenAnswer(invocation -> {
			Iterable<Long> ids = invocation.getArgument(0);
			return List.of(requestedArtist);
		});
		when(songRepository.findAll()).thenReturn(List.of(first, second, otherArtist));
		PlaylistGenerationRequest criteria = criteria(2);
		criteria.setIncludedArtistIds(List.of(11L));

		var proposals = service.generate(criteria);

		assertEquals(1, proposals.size());
		assertEquals(120, proposals.get(0).totalDurationSeconds());
		assertEquals(List.of(1L, 2L), proposals.get(0).songs().stream()
				.map(song -> song.id()).sorted().toList());
	}

	@Test
	void savesSongsInProposalOrderWithGenerationCriteria() {
		Song first = song(1L, requestedArtist, 70);
		Song second = song(2L, requestedArtist, 50);
		when(songRepository.findAllById(List.of(2L, 1L))).thenReturn(List.of(first, second));
		when(artistRepository.findAllById(any())).thenReturn(List.of(requestedArtist));
		SaveGeneratedPlaylistRequest request = new SaveGeneratedPlaylistRequest();
		request.setName("  Ma sélection  ");
		request.setCriteria(criteria(2));
		request.getCriteria().setIncludedArtistIds(List.of(11L));
		request.setSongIds(List.of(2L, 1L));

		Playlist saved = service.save("owner@example.com", request);

		assertEquals("Ma sélection", saved.getName());
		ArgumentCaptor<PlaylistSong> songs = ArgumentCaptor.forClass(PlaylistSong.class);
		verify(playlistSongRepository, times(2)).save(songs.capture());
		assertEquals(List.of(2L, 1L), songs.getAllValues().stream()
				.map(item -> item.getSongidSongs().getId()).toList());
		ArgumentCaptor<PlaylistGenerationCriteria> savedCriteria =
				ArgumentCaptor.forClass(PlaylistGenerationCriteria.class);
		verify(criteriaRepository).save(savedCriteria.capture());
		assertEquals(120, savedCriteria.getValue().getTargetDurationSeconds());
		assertArrayEquals(new Long[] {11L}, savedCriteria.getValue().getIncludedArtistIds());
	}

	@Test
	void rejectsCriteriaBothIncludedAndExcluded() {
		PlaylistGenerationRequest criteria = criteria(20);
		criteria.setIncludedArtistIds(List.of(11L));
		criteria.setExcludedArtistIds(List.of(11L));

		assertThrows(IllegalArgumentException.class, () -> service.generate(criteria));
		verify(songRepository, never()).findAll();
	}

	@Test
	void requiresIncludedFiltersAndRejectsAnyExcludedMatch() {
		Artist excludedArtist = artist(12L);
		Genre includedGenre = genre(21L);
		Genre excludedGenre = genre(22L);
		Song valid = song(1L, requestedArtist, 120);
		Song wrongArtist = song(2L, excludedArtist, 120);
		Song excludedGenreSong = song(3L, requestedArtist, 120);
		Song missingIncludedGenre = song(4L, requestedArtist, 120);
		when(artistRepository.findAllById(any())).thenReturn(List.of(requestedArtist, excludedArtist));
		when(genreRepository.findAllById(any())).thenReturn(List.of(includedGenre, excludedGenre));
		when(songRepository.findAll()).thenReturn(List.of(valid, wrongArtist, excludedGenreSong, missingIncludedGenre));
		when(songGenreRepository.findAll()).thenReturn(List.of(
				songGenre(valid, includedGenre),
				songGenre(wrongArtist, includedGenre),
				songGenre(excludedGenreSong, includedGenre),
				songGenre(excludedGenreSong, excludedGenre),
				songGenre(missingIncludedGenre, excludedGenre)));
		PlaylistGenerationRequest criteria = criteria(3);
		criteria.setIncludedGenreIds(List.of(21L));
		criteria.setExcludedGenreIds(List.of(22L));
		criteria.setIncludedArtistIds(List.of(11L));
		criteria.setExcludedArtistIds(List.of(12L));

		var proposals = service.generate(criteria);

		assertEquals(1, proposals.size());
		assertEquals(List.of(1L), proposals.get(0).songs().stream().map(song -> song.id()).toList());
	}

	private static PlaylistGenerationRequest criteria(int minutes) {
		PlaylistGenerationRequest request = new PlaylistGenerationRequest();
		request.setDurationMinutes(minutes);
		return request;
	}

	private static Song song(Long id, Artist artist, int duration) {
		Song song = new Song();
		song.setId(id);
		song.setArtistidArtists(artist);
		song.setDurationSeconds(duration);
		song.setTitle("Track " + id);
		return song;
	}

	private static Artist artist(Long id) {
		Artist artist = new Artist();
		artist.setId(id);
		artist.setStageName("Artist " + id);
		return artist;
	}

	private static Genre genre(Long id) {
		Genre genre = new Genre();
		genre.setId(id);
		genre.setName("Genre " + id);
		return genre;
	}

	private static SongGenre songGenre(Song song, Genre genre) {
		SongGenre songGenre = new SongGenre();
		songGenre.setSongidSongs(song);
		songGenre.setGenreidGenres(genre);
		return songGenre;
	}
}
