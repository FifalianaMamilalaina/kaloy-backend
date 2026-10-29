package org.example.mozika.services;

import org.example.mozika.dto.playlist.MergePlaylistsMobileRequest;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.NondownloadableSongsException;
import org.example.mozika.models.Playlist;
import org.example.mozika.models.PlaylistDownload;
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
import org.example.mozika.repositories.UsersInfosRepository;
import org.example.mozika.repositories.ArtistRepository;
import org.example.mozika.services.interfaces.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
	@Mock private UsersInfosRepository usersInfosRepository;
	@Mock private ArtistRepository artistRepository;
	@Mock private StorageService storageService;

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
				songRepository,
				usersInfosRepository,
				artistRepository,
				storageService);
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

	@Test
	void visibilityCanOnlyBeChangedByOwnerAndPublicSongsAreReadable() {
		owner.setId(1L);
		Playlist playlist = playlist(10L, owner);
		PlaylistVisibilitie publicVisibility = new PlaylistVisibilitie();
		publicVisibility.setName("PUBLIC");
		when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist));
		when(playlistVisibilitieRepository.findByName("PUBLIC")).thenReturn(Optional.of(publicVisibility));
		when(playlistSongRepository.findByPlaylistidPlaylists(playlist)).thenReturn(List.of());
		when(playlistDownloadRepository.findByUseridUsersAndPlaylistidPlaylists(owner, playlist))
				.thenReturn(Optional.empty());
		when(usersInfosRepository.findByUser(owner)).thenReturn(List.of());
		when(artistRepository.findByUseridUsers(owner)).thenReturn(List.of());

		var response = service.changeVisibility(OWNER_EMAIL, 10L, "PUBLIC");

		assertEquals("PUBLIC", response.getVisibility());
		assertEquals(true, response.isOwner());
		verify(playlistRepository).save(playlist);
	}

	@Test
	void privatePlaylistCannotBeReadThroughPublicPlaylistEndpoint() {
		Playlist playlist = playlist(10L, owner);
		PlaylistVisibilitie privateVisibility = new PlaylistVisibilitie();
		privateVisibility.setName("PRIVATE");
		playlist.setVisibilityidPlaylistVisibilities(privateVisibility);
		when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist));

		assertThrows(ResourceNotFoundException.class,
				() -> service.getPublicPlaylist(OWNER_EMAIL, 10L));
		verify(playlistSongRepository, never()).findByPlaylistidPlaylists(any());
	}

	@Test
	void anotherUserCannotChangePlaylistVisibility() {
		User another = new User();
		another.setEmail("another@example.com");

		assertThrows(ResourceNotFoundException.class,
				() -> service.changeVisibility(another.getEmail(), 10L, "PUBLIC"));
		verify(playlistRepository, never()).save(any(Playlist.class));
	}

	@Test
	void publicDiscoveryWithoutSearchUsesUnfilteredQuery() {
		when(playlistRepository.findPublicPlaylists(any())).thenReturn(new PageImpl<>(List.of()));

		service.discoverPublicPlaylists(OWNER_EMAIL, null, 0, 20);

		verify(playlistRepository).findPublicPlaylists(
				PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "updatedAt")));
		verify(playlistRepository, never()).searchPublicPlaylists(any(), any());
	}

	@Test
	void publicDiscoveryWithSearchUsesSearchQuery() {
		when(playlistRepository.searchPublicPlaylists(any(), any())).thenReturn(new PageImpl<>(List.of()));

		service.discoverPublicPlaylists(OWNER_EMAIL, "  jazz  ", 0, 20);

		verify(playlistRepository).searchPublicPlaylists(
				org.mockito.ArgumentMatchers.eq("jazz"),
				org.mockito.ArgumentMatchers.eq(
						PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "updatedAt"))));
		verify(playlistRepository, never()).findPublicPlaylists(any());
	}

	@Test
	void offlineRequestAllowsOwnerAndReturnsOrderedSongMetadataAndFreshUrl() {
		Playlist playlist = playlist(10L, owner);
		Song first = downloadableSong(1L, "first-key");
		Song second = downloadableSong(2L, "second-key");
		PlaylistSong firstLink = playlistSong(playlist, first, 1);
		PlaylistSong secondLink = playlistSong(playlist, second, 2);
		when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist));
		when(playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(playlist))
				.thenReturn(List.of(firstLink, secondLink));
		when(playlistDownloadRepository.findByUseridUsersAndPlaylistidPlaylists(owner, playlist))
				.thenReturn(Optional.empty());
		when(playlistDownloadRepository.findByUseridUsersAndIsActiveTrue(owner)).thenReturn(List.of());
		when(playlistDownloadRepository.countByUseridUsersAndIsActiveTrueAndExpiresAtAfter(any(), any()))
				.thenReturn(0L);
		when(playlistDownloadRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		when(storageService.getPresignedUrl("first-key", 24, TimeUnit.HOURS)).thenReturn("https://minio/first");
		when(storageService.getPresignedUrl("second-key", 24, TimeUnit.HOURS)).thenReturn("https://minio/second");

		var response = service.requestOfflineDownload(OWNER_EMAIL, 10L);

		assertEquals(10L, response.playlistId());
		assertEquals(List.of(1L, 2L), response.songs().stream().map(song -> song.songId()).toList());
		assertEquals(List.of("https://minio/first", "https://minio/second"),
				response.songs().stream().map(song -> song.downloadUrl()).toList());
		assertEquals(true, response.active());
	}

	@Test
	void offlineUrlsSupportProtectedUploadsAndAbsoluteAudioSources() {
		Playlist playlist = playlist(10L, owner);
		PlaylistSong upload = playlistSong(playlist, downloadableSong(1L, "/uploads/audio.m4a"), 1);
		PlaylistSong external = playlistSong(
				playlist, downloadableSong(2L, "https://cdn.example/audio.mp3"), 2);
		when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist));
		when(playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(playlist))
				.thenReturn(List.of(upload, external));
		when(playlistDownloadRepository.findByUseridUsersAndPlaylistidPlaylists(owner, playlist))
				.thenReturn(Optional.empty());
		when(playlistDownloadRepository.findByUseridUsersAndIsActiveTrue(owner)).thenReturn(List.of());
		when(playlistDownloadRepository.countByUseridUsersAndIsActiveTrueAndExpiresAtAfter(any(), any()))
				.thenReturn(0L);
		when(playlistDownloadRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		ReflectionTestUtils.setField(service, "contextPath", "/mozika");

		var response = service.requestOfflineDownload(OWNER_EMAIL, 10L);

		assertEquals("/mozika/media/songs/1/offline-download", response.songs().get(0).downloadUrl());
		assertEquals("https://cdn.example/audio.mp3", response.songs().get(1).downloadUrl());
		verify(storageService, never()).getPresignedUrl(any(), anyInt(), any());
	}

	@Test
	void offlineRequestAcceptsMinioObjectKeysContainingSpacesWithoutChangingThem() {
		Playlist playlist = playlist(3L, owner);
		Song first = downloadableSong(1L, "Eh sambatra sy tretrika - Vetsonkira(M4A_128K)-audio.m4a");
		Song second = downloadableSong(2L, "Reko/Gasy Tsara.mp3");
		when(playlistRepository.findById(3L)).thenReturn(Optional.of(playlist));
		when(playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(playlist))
				.thenReturn(List.of(playlistSong(playlist, first, 0), playlistSong(playlist, second, 1)));
		when(playlistDownloadRepository.findByUseridUsersAndPlaylistidPlaylists(owner, playlist))
				.thenReturn(Optional.empty());
		when(playlistDownloadRepository.findByUseridUsersAndIsActiveTrue(owner)).thenReturn(List.of());
		when(playlistDownloadRepository.countByUseridUsersAndIsActiveTrueAndExpiresAtAfter(any(), any()))
				.thenReturn(0L);
		when(playlistDownloadRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		when(storageService.getPresignedUrl(first.getAudioUrl(), 24, TimeUnit.HOURS))
				.thenReturn("https://minio/first");
		when(storageService.getPresignedUrl(second.getAudioUrl(), 24, TimeUnit.HOURS))
				.thenReturn("https://minio/second");

		var response = service.requestOfflineDownload(OWNER_EMAIL, 3L);

		assertEquals(List.of("https://minio/first", "https://minio/second"),
				response.songs().stream().map(song -> song.downloadUrl()).toList());
		verify(storageService).getPresignedUrl(first.getAudioUrl(), 24, TimeUnit.HOURS);
		verify(storageService).getPresignedUrl(second.getAudioUrl(), 24, TimeUnit.HOURS);
	}

	@Test
	void offlineRequestAllowsPublicPlaylistButRejectsPrivateNonOwner() {
		User viewer = new User();
		viewer.setEmail("viewer@example.com");
		when(userRepository.findByEmail(viewer.getEmail())).thenReturn(Optional.of(viewer));
		Playlist playlist = playlist(10L, owner);
		PlaylistVisibilitie publicVisibility = new PlaylistVisibilitie();
		publicVisibility.setName("PUBLIC");
		playlist.setVisibilityidPlaylistVisibilities(publicVisibility);
		when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist));
		when(playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(playlist)).thenReturn(List.of());
		when(playlistDownloadRepository.findByUseridUsersAndPlaylistidPlaylists(viewer, playlist))
				.thenReturn(Optional.empty());
		when(playlistDownloadRepository.findByUseridUsersAndIsActiveTrue(viewer)).thenReturn(List.of());
		when(playlistDownloadRepository.countByUseridUsersAndIsActiveTrueAndExpiresAtAfter(any(), any()))
				.thenReturn(0L);
		when(playlistDownloadRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		assertEquals(10L, service.requestOfflineDownload(viewer.getEmail(), 10L).playlistId());

		playlist.setVisibilityidPlaylistVisibilities(new PlaylistVisibilitie());
		assertThrows(ResourceNotFoundException.class,
				() -> service.requestOfflineDownload(viewer.getEmail(), 10L));
	}

	@Test
	void nondownloadableSongIdsAreReportedWithoutCreatingDownload() {
		Playlist playlist = playlist(10L, owner);
		PlaylistSong link = playlistSong(playlist, song(42L), 1);
		when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist));
		when(playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(playlist)).thenReturn(List.of(link));

		NondownloadableSongsException exception = assertThrows(NondownloadableSongsException.class,
				() -> service.requestOfflineDownload(OWNER_EMAIL, 10L));

		assertEquals(List.of(42L), exception.getSongIds());
		verify(playlistDownloadRepository, never()).save(any());
		verify(playlistDownloadRepository, never()).countByUseridUsersAndIsActiveTrueAndExpiresAtAfter(any(), any());
	}

	@Test
	void offlineRequestStillRejectsSongWhoseDownloadFlagIsFalse() {
		Playlist playlist = playlist(10L, owner);
		Song song = downloadableSong(44L, "Reko/Gasy Tsara.mp3");
		song.setIsDownloadable(false);
		when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist));
		when(playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(playlist))
				.thenReturn(List.of(playlistSong(playlist, song, 0)));

		NondownloadableSongsException exception = assertThrows(NondownloadableSongsException.class,
				() -> service.requestOfflineDownload(OWNER_EMAIL, 10L));

		assertEquals(List.of(44L), exception.getSongIds());
		verify(storageService, never()).getPresignedUrl(any(), anyInt(), any());
		verify(playlistDownloadRepository, never()).save(any());
	}

	@Test
	void publicCleartextAudioUrlIsRejectedForOfflineDownload() {
		Playlist playlist = playlist(10L, owner);
		PlaylistSong link = playlistSong(
				playlist, downloadableSong(43L, "http://cdn.example/audio.mp3"), 1);
		when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist));
		when(playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(playlist)).thenReturn(List.of(link));

		NondownloadableSongsException exception = assertThrows(NondownloadableSongsException.class,
				() -> service.requestOfflineDownload(OWNER_EMAIL, 10L));

		assertEquals(List.of(43L), exception.getSongIds());
		verify(playlistDownloadRepository, never()).save(any());
	}

	@Test
	void offlineQuotaReturnsTooManyRequests() {
		Playlist playlist = playlist(10L, owner);
		when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist));
		when(playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(playlist)).thenReturn(List.of());
		when(playlistDownloadRepository.findByUseridUsersAndPlaylistidPlaylists(owner, playlist))
				.thenReturn(Optional.empty());
		when(playlistDownloadRepository.findByUseridUsersAndIsActiveTrue(owner)).thenReturn(List.of());
		when(playlistDownloadRepository.countByUseridUsersAndIsActiveTrueAndExpiresAtAfter(any(), any()))
				.thenReturn(10L);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> service.requestOfflineDownload(OWNER_EMAIL, 10L));

		assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getStatusCode());
		verify(playlistDownloadRepository, never()).save(any());
	}

	@Test
	void activeOfflineRequestIsIdempotentAndExpiredEntriesAreReactivatedWithinQuota() {
		Playlist playlist = playlist(10L, owner);
		PlaylistDownload existing = offlineRecord(owner, playlist, true, LocalDateTime.now().plusDays(5));
		when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist));
		when(playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(playlist)).thenReturn(List.of());
		when(playlistDownloadRepository.findByUseridUsersAndPlaylistidPlaylists(owner, playlist))
				.thenReturn(Optional.of(existing));

		service.requestOfflineDownload(OWNER_EMAIL, 10L);

		verify(playlistDownloadRepository, never()).countByUseridUsersAndIsActiveTrueAndExpiresAtAfter(any(), any());
		verify(playlistDownloadRepository, never()).save(any());
	}

	@Test
	void offlineListingReportsExpiredAsInactiveAndDeleteSoftDeactivates() {
		Playlist playlist = playlist(10L, owner);
		PlaylistDownload expired = offlineRecord(owner, playlist, true,
				LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1));
		when(playlistDownloadRepository.findByUseridUsers(owner)).thenReturn(List.of(expired));
		when(playlistDownloadRepository.findByUseridUsersAndIsActiveTrue(owner)).thenReturn(List.of(expired));
		when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist));
		when(playlistDownloadRepository.findByUseridUsersAndPlaylistidPlaylists(owner, playlist))
				.thenReturn(Optional.of(expired));
		when(playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(playlist)).thenReturn(List.of());

		var listed = service.listOfflineDownloads(OWNER_EMAIL);
		assertEquals(false, listed.get(0).active());
		assertEquals(10L, listed.get(0).playlistId());
		verify(playlistDownloadRepository).saveAll(List.of(expired));

		service.deleteOfflineDownload(OWNER_EMAIL, 10L);
		assertEquals(false, expired.getIsActive());
		verify(playlistDownloadRepository).save(expired);
	}

	@Test
	void refreshRenewsExpiryAndReactivatesAnExpiredDownload() {
		Playlist playlist = playlist(10L, owner);
		PlaylistDownload expired = offlineRecord(owner, playlist, true, LocalDateTime.now().minusDays(1));
		when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist));
		when(playlistDownloadRepository.findByUseridUsersAndPlaylistidPlaylists(owner, playlist))
				.thenReturn(Optional.of(expired));
		when(playlistDownloadRepository.findByUseridUsersAndIsActiveTrue(owner)).thenReturn(List.of(expired));
		when(playlistDownloadRepository.countByUseridUsersAndIsActiveTrueAndExpiresAtAfter(any(), any()))
				.thenReturn(0L);
		when(playlistDownloadRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		when(playlistSongRepository.findByPlaylistidPlaylistsOrderByPositionAsc(playlist)).thenReturn(List.of());

		var response = service.refreshOfflineDownload(OWNER_EMAIL, 10L);

		assertEquals(true, response.active());
		assertEquals(true, expired.getExpiresAt().isAfter(LocalDateTime.now(ZoneOffset.UTC).plusDays(29)));
		assertEquals(true, expired.getIsActive());
	}

	private static Song downloadableSong(Long id, String audioUrl) {
		Song song = song(id);
		song.setTitle("Song " + id);
		song.setIsDownloadable(true);
		song.setAudioUrl(audioUrl);
		return song;
	}

	private static PlaylistDownload offlineRecord(
			User user, Playlist playlist, boolean active, LocalDateTime expiresAt) {
		PlaylistDownload download = new PlaylistDownload();
		download.setUseridUsers(user);
		download.setPlaylistidPlaylists(playlist);
		download.setDownloadedAt(LocalDateTime.now().minusDays(2));
		download.setExpiresAt(expiresAt);
		download.setIsActive(active);
		return download;
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
