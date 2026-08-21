package org.example.mozika.services.interfaces;

import org.example.mozika.models.Genre;
import org.example.mozika.models.SongGenre;

import org.example.mozika.models.dto.GenreSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface GenreService {
    Page<Genre> getAllGenre(Pageable pageable);

    Page<Genre> getAllGenre(Pageable pageable, GenreSearch object);

    Genre getGenreById(Long id);

    public String exportGenreToCSV(List<Genre> genre);

    

    Genre createGenre(Genre genre);

    Genre updateGenre(Long id, Genre genre);

    void deleteGenre(Long id);
    

    Genre createFullGenre(Genre genre,List<SongGenre> songGenres);
    Genre updateFullGenre(Long id, Genre genre,List<SongGenre> songGenres);
    void deleteFullGenre(Long id);
}
