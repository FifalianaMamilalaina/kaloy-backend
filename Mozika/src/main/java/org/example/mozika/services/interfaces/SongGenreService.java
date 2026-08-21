package org.example.mozika.services.interfaces;

import org.example.mozika.models.SongGenre;
import org.example.mozika.models.dto.SongGenreSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface SongGenreService {
    Page<SongGenre> getAllSongGenre(Pageable pageable);

    Page<SongGenre> getAllSongGenre(Pageable pageable, SongGenreSearch object);

    SongGenre getSongGenreById(Long id);

    public String exportSongGenreToCSV(List<SongGenre> songGenre);

    

    SongGenre createSongGenre(SongGenre songGenre);

    SongGenre updateSongGenre(Long id, SongGenre songGenre);

    void deleteSongGenre(Long id);
    

}
