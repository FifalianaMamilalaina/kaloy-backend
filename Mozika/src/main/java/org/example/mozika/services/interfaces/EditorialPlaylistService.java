package org.example.mozika.services.interfaces;

import org.example.mozika.models.EditorialPlaylist;
import org.example.mozika.models.dto.EditorialPlaylistSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface EditorialPlaylistService {
    Page<EditorialPlaylist> getAllEditorialPlaylist(Pageable pageable);

    Page<EditorialPlaylist> getAllEditorialPlaylist(Pageable pageable, EditorialPlaylistSearch object);

    EditorialPlaylist getEditorialPlaylistById(Long id);

    public String exportEditorialPlaylistToCSV(List<EditorialPlaylist> editorialPlaylist);

    

    EditorialPlaylist createEditorialPlaylist(EditorialPlaylist editorialPlaylist);

    EditorialPlaylist updateEditorialPlaylist(Long id, EditorialPlaylist editorialPlaylist);

    void deleteEditorialPlaylist(Long id);
    

}
