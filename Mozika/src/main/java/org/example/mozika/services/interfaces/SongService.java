package org.example.mozika.services.interfaces;

import org.example.mozika.models.Song;
import org.example.mozika.models.EditorialPlaylistSong;
import org.example.mozika.models.ListeningHistory;
import org.example.mozika.models.PlaylistSong;
import org.example.mozika.models.SongGenre;
import org.example.mozika.models.UpNextQueue;

import org.example.mozika.models.dto.SongSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface SongService {
    Page<Song> getAllSong(Pageable pageable);

    Page<Song> getAllSong(Pageable pageable, SongSearch object);

    Song getSongById(Long id);

    public String exportSongToCSV(List<Song> song);

    

    Song createSong(Song song);

    Song updateSong(Long id, Song song);

    void deleteSong(Long id);
    

    Song createFullSong(Song song,List<EditorialPlaylistSong> editorialPlaylistSongs,List<ListeningHistory> listeningHistorys,List<PlaylistSong> playlistSongs,List<SongGenre> songGenres,List<UpNextQueue> upNextQueues);
    Song updateFullSong(Long id, Song song,List<EditorialPlaylistSong> editorialPlaylistSongs,List<ListeningHistory> listeningHistorys,List<PlaylistSong> playlistSongs,List<SongGenre> songGenres,List<UpNextQueue> upNextQueues);
    void deleteFullSong(Long id);
}
