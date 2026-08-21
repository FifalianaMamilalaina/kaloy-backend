package org.example.mozika.services.interfaces;

import org.example.mozika.models.PlaylistSong;
import org.example.mozika.models.dto.PlaylistSongSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface PlaylistSongService {
    Page<PlaylistSong> getAllPlaylistSong(Pageable pageable);

    Page<PlaylistSong> getAllPlaylistSong(Pageable pageable, PlaylistSongSearch object);

    PlaylistSong getPlaylistSongById(Long id);

    public String exportPlaylistSongToCSV(List<PlaylistSong> playlistSong);

    

    PlaylistSong createPlaylistSong(PlaylistSong playlistSong);

    PlaylistSong updatePlaylistSong(Long id, PlaylistSong playlistSong);

    void deletePlaylistSong(Long id);
    

}
