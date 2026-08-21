package org.example.mozika.models.dto;

import org.example.mozika.models.Playlist;

import org.example.mozika.models.Download;
import org.example.mozika.models.PlaylistSong;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlaylistFullDto {
    private Playlist playlist;
    
    private List<Download> downloads;
    private List<PlaylistSong> playlistSongs;
    
}
