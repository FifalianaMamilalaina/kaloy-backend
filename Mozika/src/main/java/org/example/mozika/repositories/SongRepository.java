package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.Song;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.Artist;
import org.example.mozika.models.Album;



public interface SongRepository extends JpaRepository<Song, Long>, JpaSpecificationExecutor<Song> {

List<Song> findByArtistidArtists(Artist artistidArtists);
List<Song> findByAlbumidAlbums(Album albumidAlbums);



}
