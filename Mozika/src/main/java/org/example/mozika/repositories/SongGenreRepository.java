package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.SongGenre;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.Song;
import org.example.mozika.models.Genre;



public interface SongGenreRepository extends JpaRepository<SongGenre, Long>, JpaSpecificationExecutor<SongGenre> {

List<SongGenre> findBySongidSongs(Song songidSongs);
List<SongGenre> findByGenreidGenres(Genre genreidGenres);



}
