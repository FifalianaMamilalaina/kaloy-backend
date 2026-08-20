package org.example.mozika.services.interfaces;

import org.example.mozika.models.Playlist;
import org.example.mozika.models.Download;
import org.example.mozika.models.PlaylistSong;

import org.example.mozika.models.dto.PlaylistSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface PlaylistService {
    Page<Playlist> getAllPlaylist(Pageable pageable);

    Page<Playlist> getAllPlaylist(Pageable pageable, PlaylistSearch object);

    Playlist getPlaylistById(Long id);

    public String exportPlaylistToCSV(List<Playlist> playlist);

    

    Playlist createPlaylist(Playlist playlist);

    Playlist updatePlaylist(Long id, Playlist playlist);

    void deletePlaylist(Long id);
    

    Playlist createFullPlaylist(Playlist playlist,List<Download> downloads,List<PlaylistSong> playlistSongs);
    Playlist updateFullPlaylist(Long id, Playlist playlist,List<Download> downloads,List<PlaylistSong> playlistSongs);
    void deleteFullPlaylist(Long id);
}
