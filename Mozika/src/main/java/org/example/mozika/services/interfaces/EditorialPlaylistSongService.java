package org.example.mozika.services.interfaces;

import org.example.mozika.models.EditorialPlaylistSong;
import org.example.mozika.models.dto.EditorialPlaylistSongSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface EditorialPlaylistSongService {
    Page<EditorialPlaylistSong> getAllEditorialPlaylistSong(Pageable pageable);

    Page<EditorialPlaylistSong> getAllEditorialPlaylistSong(Pageable pageable, EditorialPlaylistSongSearch object);

    EditorialPlaylistSong getEditorialPlaylistSongById(Long id);

    public String exportEditorialPlaylistSongToCSV(List<EditorialPlaylistSong> editorialPlaylistSong);

    

    EditorialPlaylistSong createEditorialPlaylistSong(EditorialPlaylistSong editorialPlaylistSong);

    EditorialPlaylistSong updateEditorialPlaylistSong(Long id, EditorialPlaylistSong editorialPlaylistSong);

    void deleteEditorialPlaylistSong(Long id);
    

}
