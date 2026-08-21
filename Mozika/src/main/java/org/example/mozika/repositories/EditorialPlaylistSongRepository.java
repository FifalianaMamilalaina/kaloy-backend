package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.EditorialPlaylistSong;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.Song;



public interface EditorialPlaylistSongRepository extends JpaRepository<EditorialPlaylistSong, Long>, JpaSpecificationExecutor<EditorialPlaylistSong> {

List<EditorialPlaylistSong> findBySongidSongs(Song songidSongs);



}
