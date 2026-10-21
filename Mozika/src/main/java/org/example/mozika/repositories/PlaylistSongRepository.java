package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.PlaylistSong;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import org.example.mozika.models.Playlist;
import org.example.mozika.models.Song;



public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long>, JpaSpecificationExecutor<PlaylistSong> {

List<PlaylistSong> findByPlaylistidPlaylists(Playlist playlistidPlaylists);
List<PlaylistSong> findByPlaylistidPlaylistsOrderByPositionAsc(Playlist playlistidPlaylists);
List<PlaylistSong> findBySongidSongs(Song songidSongs);
Optional<PlaylistSong> findByPlaylistidPlaylistsAndSongidSongs(Playlist playlist, Song song);



}
