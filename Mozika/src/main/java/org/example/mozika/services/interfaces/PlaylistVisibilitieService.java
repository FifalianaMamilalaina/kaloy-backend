package org.example.mozika.services.interfaces;

import org.example.mozika.models.PlaylistVisibilitie;
import org.example.mozika.models.dto.PlaylistVisibilitieSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface PlaylistVisibilitieService {
    Page<PlaylistVisibilitie> getAllPlaylistVisibilitie(Pageable pageable);

    Page<PlaylistVisibilitie> getAllPlaylistVisibilitie(Pageable pageable, PlaylistVisibilitieSearch object);

    PlaylistVisibilitie getPlaylistVisibilitieById(Long id);

    public String exportPlaylistVisibilitieToCSV(List<PlaylistVisibilitie> playlistVisibilitie);

    

    PlaylistVisibilitie createPlaylistVisibilitie(PlaylistVisibilitie playlistVisibilitie);

    PlaylistVisibilitie updatePlaylistVisibilitie(Long id, PlaylistVisibilitie playlistVisibilitie);

    void deletePlaylistVisibilitie(Long id);
    

}
