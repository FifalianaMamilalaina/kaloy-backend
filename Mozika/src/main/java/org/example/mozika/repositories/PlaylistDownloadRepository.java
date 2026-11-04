package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.example.mozika.models.Playlist;
import org.example.mozika.models.PlaylistDownload;
import org.example.mozika.models.User;

import java.util.List;
import java.util.Optional;

public interface PlaylistDownloadRepository
		extends JpaRepository<PlaylistDownload, Long>, JpaSpecificationExecutor<PlaylistDownload> {

	List<PlaylistDownload> findByUseridUsersAndIsActiveTrue(User user);

	List<PlaylistDownload> findByUseridUsers(User user);

	Optional<PlaylistDownload> findByUseridUsersAndPlaylistidPlaylists(User user, Playlist playlist);

	long countByUseridUsersAndIsActiveTrueAndExpiresAtAfter(User user, java.time.LocalDateTime now);
}
