package org.example.mozika.services;

import org.example.mozika.dto.playlist.MergePlaylistsMobileRequest;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.Playlist;
import org.example.mozika.models.PlaylistSong;
import org.example.mozika.models.PlaylistVisibilitie;
import org.example.mozika.models.Song;
import org.example.mozika.models.User;
import org.example.mozika.repositories.EditorialPlaylistRepository;
import org.example.mozika.repositories.PlaylistDownloadRepository;
import org.example.mozika.repositories.PlaylistFusionRepository;
import org.example.mozika.repositories.PlaylistRepository;
import org.example.mozika.repositories.PlaylistSongRepository;
import org.example.mozika.repositories.PlaylistVisibilitieRepository;
import org.example.mozika.repositories.SongRepository;
import org.example.mozika.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class DefaultPlaylistMobileServiceTest {

	private static final String OWNER_EMAIL = "owner@example.com";

	@Mock private PlaylistRepository playlistRepository;
	@Mock private PlaylistSongRepository playlistSongRepository;
	@Mock private PlaylistDownloadRepository playlistDownloadRepository;
	@Mock private PlaylistFusionRepository playlistFusionRepository;
	@Mock private PlaylistVisibilitieRepository playlistVisibilitieRepository;
	@Mock private EditorialPlaylistRepository editorialPlaylistRepository;
	@Mock private UserRepository userRepository;
	@Mock private SongRepository songRepository;

	private DefaultPlaylistMobileService service;
	private User owner;

	@BeforeEach
	void setUp() {
		service = new DefaultPlaylistMobileService(
				playlistRepository,
				playlistSongRepository,
				playlistDownloadRepository,
				playlistFusionRepository,
				playlistVisibilitieRepository,
				editorialPlaylistRepository,
				userRepository,
				songRepository);
		owner = new User();
		owner.setEmail(OWNER_EMAIL);
		lenient().when(userRepository.findByEmail(OWNER_EMAIL)).thenReturn(Optional.of(owner));
		lenient().when(playlistVisibilitieRepository.findByName("PRIVATE"))
				.thenReturn(Optional.of(new PlaylistVisibilitie()));
		lenient().when(playlistRepository.save(any(Playlist.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));
		lenient().when(playlistSongRepository.save(any(PlaylistSong.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));
		lenient().when(playlistFusionRepository.save(any()))
				.thenAnswer(invocation -> invocation.getArgument(0));
	}

	@Test
	void mergePreservesRequestedSourceAndSongOrderAndKeepsFirstDuplicate() {
		Playlist sourceA = playlist(10L, owner);
		Playlist sourceB = playlist(20L, owner);
		Song songA = song(1L);
		Song songB = song(2L);
		Song songBFromSecondSource = song(2L);
		Song songC = song(3L);

		when(playlistRepository.findById(20L)).thenReturn(Optional.of(sourceB));
		when(playlistRepository.findById(10L)).thenReturn(Optional.of(sourceA));
		when(playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(sourceB))
				.thenReturn(List.of(playlistSong(sourceB, songA, 2), playlistSong(sourceB, songB, 5)));
		when(playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(sourceA))
				.thenReturn(List.of(playlistSong(sourceA, songBFromSecondSource, 1), playlistSong(sourceA, songC, 3)));

		MergePlaylistsMobileRequest request = new MergePlaylistsMobileRequest();
		request.setSourceIds(List.of(20L, 10L));
		request.setResultName("  Mix final  ");

		Playlist result = service.mergePlaylist(OWNER_EMAIL, request);

		assertEquals("Mix final", result.getName());
		ArgumentCaptor<PlaylistSong> captor = ArgumentCaptor.forClass(PlaylistSong.class);
		verify(playlistSongRepository, times(3)).save(captor.capture());
		List<PlaylistSong> mergedSongs = captor.getAllValues();
		assertEquals(List.of(1L, 2L, 3L), mergedSongs.stream()
				.map(playlistSong -> playlistSong.getSongidSongs().getId()).toList());
		assertEquals(List.of(1, 2, 3), mergedSongs.stream()
				.map(PlaylistSong::getPosition).toList());
		verify(playlistFusionRepository, times(2)).save(any());
	}

	@Test
	void mergeRejectsSourcePlaylistNotOwnedByCurrentUser() {
		User anotherUser = new User();
		anotherUser.setEmail("another@example.com");
		when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist(10L, owner)));
		when(playlistRepository.findById(20L)).thenReturn(Optional.of(playlist(20L, anotherUser)));

		MergePlaylistsMobileRequest request = new MergePlaylistsMobileRequest();
		request.setSourceIds(List.of(10L, 20L));
		request.setResultName("Mix");

		assertThrows(ResourceNotFoundException.class, () -> service.mergePlaylist(OWNER_EMAIL, request));
		verify(playlistRepository, never()).save(any(Playlist.class));
	}

	@Test
	void mergeRejectsDuplicateSourcePlaylistIds() {
		MergePlaylistsMobileRequest request = new MergePlaylistsMobileRequest();
		request.setSourceIds(List.of(10L, 10L));
		request.setResultName("Mix");

		assertThrows(IllegalArgumentException.class, () -> service.mergePlaylist(OWNER_EMAIL, request));
		verify(userRepository, never()).findByEmail(any());
		verify(playlistRepository, never()).save(any(Playlist.class));
	}

	private static Playlist playlist(Long id, User owner) {
		Playlist playlist = new Playlist();
		playlist.setId(id);
		playlist.setOwneruseridUsers(owner);
		return playlist;
	}

	private static Song song(Long id) {
		Song song = new Song();
		song.setId(id);
		return song;
	}

	private static PlaylistSong playlistSong(Playlist playlist, Song song, int position) {
		PlaylistSong playlistSong = new PlaylistSong();
		playlistSong.setPlaylistidPlaylists(playlist);
		playlistSong.setSongidSongs(song);
		playlistSong.setPosition(position);
		return playlistSong;
	}
}
