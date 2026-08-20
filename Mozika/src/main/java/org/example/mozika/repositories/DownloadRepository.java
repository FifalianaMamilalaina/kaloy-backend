package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.Download;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.User;
import org.example.mozika.models.Playlist;



public interface DownloadRepository extends JpaRepository<Download, Long>, JpaSpecificationExecutor<Download> {

List<Download> findByUseridUsers(User useridUsers);
List<Download> findByPlaylistidPlaylists(Playlist playlistidPlaylists);



}
