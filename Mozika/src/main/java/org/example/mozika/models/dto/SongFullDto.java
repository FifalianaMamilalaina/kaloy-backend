package org.example.mozika.models.dto;

import org.example.mozika.models.Song;

import org.example.mozika.models.EditorialPlaylistSong;
import org.example.mozika.models.ListeningHistory;
import org.example.mozika.models.PlaylistSong;
import org.example.mozika.models.SongGenre;
import org.example.mozika.models.UpNextQueue;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SongFullDto {
    private Song song;
    
    private List<EditorialPlaylistSong> editorialPlaylistSongs;
    private List<ListeningHistory> listeningHistorys;
    private List<PlaylistSong> playlistSongs;
    private List<SongGenre> songGenres;
    private List<UpNextQueue> upNextQueues;
    
}
