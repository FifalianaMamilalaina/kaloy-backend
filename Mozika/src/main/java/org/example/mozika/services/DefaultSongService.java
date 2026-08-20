package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.Song;
import org.example.mozika.models.dto.SongSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.SongRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.SongService;
import org.example.mozika.specification.SongSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;

import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import org.example.mozika.repositories.EditorialPlaylistSongRepository;
import org.example.mozika.models.EditorialPlaylistSong;
import org.example.mozika.repositories.ListeningHistoryRepository;
import org.example.mozika.models.ListeningHistory;
import org.example.mozika.repositories.PlaylistSongRepository;
import org.example.mozika.models.PlaylistSong;
import org.example.mozika.repositories.SongGenreRepository;
import org.example.mozika.models.SongGenre;
import org.example.mozika.repositories.UpNextQueueRepository;
import org.example.mozika.models.UpNextQueue;


@Service
public class DefaultSongService implements SongService {
	private final SongRepository songRepository;
private final EditorialPlaylistSongRepository editorialPlaylistSongRepository;
private final ListeningHistoryRepository listeningHistoryRepository;
private final PlaylistSongRepository playlistSongRepository;
private final SongGenreRepository songGenreRepository;
private final UpNextQueueRepository upNextQueueRepository;


	public DefaultSongService(SongRepository songRepository, EditorialPlaylistSongRepository editorialPlaylistSongRepository, ListeningHistoryRepository listeningHistoryRepository, PlaylistSongRepository playlistSongRepository, SongGenreRepository songGenreRepository, UpNextQueueRepository upNextQueueRepository) {
	   this.songRepository = songRepository;
this.editorialPlaylistSongRepository = editorialPlaylistSongRepository;
this.listeningHistoryRepository = listeningHistoryRepository;
this.playlistSongRepository = playlistSongRepository;
this.songGenreRepository = songGenreRepository;
this.upNextQueueRepository = upNextQueueRepository;

	}

	@Override
	public String exportSongToCSV(List<Song> song) {
	   return ExportUtils.generateCsv(song);
	}  

	@Override
	public Page<Song> getAllSong(Pageable pageable) {
	    try {
	        return songRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving song", ex);
	    }
	}

	@Override
	public Page<Song> getAllSong(Pageable pageable, SongSearch object) {
	    try {
	        Specification<Song> spec=SongSpecification.filter(object);
	        return songRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving song", ex);
	    }
	}

	@Override
	public Song getSongById(Long id) {
	    Optional<Song> song = songRepository.findById(id);
	    if (song.isPresent()) {
	        return song.get();
	    } else {
	        throw new ResourceNotFoundException("Song not found with id : " + id);
	    }
	}

	@Override
	public Song createSong(Song song) {
	    try {
	        return songRepository.save(song);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating song", ex);
	    }
	}

	@Override
	public Song updateSong(Long id, Song song) {
	    Optional<Song> existingSong = songRepository.findById(id);
	    if (existingSong.isPresent()) {
	        song.setId(id);
	        try {
	            return songRepository.save(song);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating song", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("Song not found with id : " + id);
	    }
	}

	@Override
	public void deleteSong(Long id) {
	    try {
	        songRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting song", ex);
	    }
	}


@Override
@Transactional
public Song createFullSong(Song song, List<EditorialPlaylistSong> editorialPlaylistSongs, List<ListeningHistory> listeningHistorys, List<PlaylistSong> playlistSongs, List<SongGenre> songGenres, List<UpNextQueue> upNextQueues) {
  try {
    song = songRepository.save(song);
    
    for (EditorialPlaylistSong editorialPlaylistSong : editorialPlaylistSongs) {
      editorialPlaylistSong.setSongidSongs(song);
      editorialPlaylistSong.setId(null);
    }
    
    editorialPlaylistSongRepository.saveAll(editorialPlaylistSongs);
    for (ListeningHistory listeningHistory : listeningHistorys) {
      listeningHistory.setSongidSongs(song);
      listeningHistory.setId(null);
    }
    
    listeningHistoryRepository.saveAll(listeningHistorys);
    for (PlaylistSong playlistSong : playlistSongs) {
      playlistSong.setSongidSongs(song);
      playlistSong.setId(null);
    }
    
    playlistSongRepository.saveAll(playlistSongs);
    for (SongGenre songGenre : songGenres) {
      songGenre.setSongidSongs(song);
      songGenre.setId(null);
    }
    
    songGenreRepository.saveAll(songGenres);
    for (UpNextQueue upNextQueue : upNextQueues) {
      upNextQueue.setSongidSongs(song);
      upNextQueue.setId(null);
    }
    
    upNextQueueRepository.saveAll(upNextQueues);
    
    
    return song;
  } catch (Exception ex) {
    throw new InternalServerErrorException(
    "Error during atomic creation of Song and its details.", ex);
  }
}
  
@Override
@Transactional
public Song updateFullSong(Long id, Song song, List<EditorialPlaylistSong> editorialPlaylistSongs, List<ListeningHistory> listeningHistorys, List<PlaylistSong> playlistSongs, List<SongGenre> songGenres, List<UpNextQueue> upNextQueues) {
  try {
    if (!songRepository.existsById(id)) {
      throw new ResourceNotFoundException("Song not found with id: " + id);
    }

    song.setId(id);
    song = songRepository.save(song);

    List<EditorialPlaylistSong> existingEditorialPlaylistSongs = editorialPlaylistSongRepository.findBySongidSongs(song);

    List<EditorialPlaylistSong> editorialPlaylistSongsToDelete = existingEditorialPlaylistSongs.stream()
      .filter(existing -> editorialPlaylistSongs.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!editorialPlaylistSongsToDelete.isEmpty()) {
      editorialPlaylistSongRepository.deleteAll(editorialPlaylistSongsToDelete);
    }

    for (EditorialPlaylistSong editorialPlaylistSong : editorialPlaylistSongs) {
      editorialPlaylistSong.setSongidSongs(song);
      
      if (editorialPlaylistSong.getId() != null && 
          existingEditorialPlaylistSongs.stream()
            .anyMatch(e -> e.getId().equals(editorialPlaylistSong.getId()))) {
          editorialPlaylistSongRepository.save(editorialPlaylistSong);
      } else {
          editorialPlaylistSong.setId(null);
          editorialPlaylistSongRepository.save(editorialPlaylistSong);
      }
    }
    List<ListeningHistory> existingListeningHistorys = listeningHistoryRepository.findBySongidSongs(song);

    List<ListeningHistory> listeningHistorysToDelete = existingListeningHistorys.stream()
      .filter(existing -> listeningHistorys.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!listeningHistorysToDelete.isEmpty()) {
      listeningHistoryRepository.deleteAll(listeningHistorysToDelete);
    }

    for (ListeningHistory listeningHistory : listeningHistorys) {
      listeningHistory.setSongidSongs(song);
      
      if (listeningHistory.getId() != null && 
          existingListeningHistorys.stream()
            .anyMatch(e -> e.getId().equals(listeningHistory.getId()))) {
          listeningHistoryRepository.save(listeningHistory);
      } else {
          listeningHistory.setId(null);
          listeningHistoryRepository.save(listeningHistory);
      }
    }
    List<PlaylistSong> existingPlaylistSongs = playlistSongRepository.findBySongidSongs(song);

    List<PlaylistSong> playlistSongsToDelete = existingPlaylistSongs.stream()
      .filter(existing -> playlistSongs.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!playlistSongsToDelete.isEmpty()) {
      playlistSongRepository.deleteAll(playlistSongsToDelete);
    }

    for (PlaylistSong playlistSong : playlistSongs) {
      playlistSong.setSongidSongs(song);
      
      if (playlistSong.getId() != null && 
          existingPlaylistSongs.stream()
            .anyMatch(e -> e.getId().equals(playlistSong.getId()))) {
          playlistSongRepository.save(playlistSong);
      } else {
          playlistSong.setId(null);
          playlistSongRepository.save(playlistSong);
      }
    }
    List<SongGenre> existingSongGenres = songGenreRepository.findBySongidSongs(song);

    List<SongGenre> songGenresToDelete = existingSongGenres.stream()
      .filter(existing -> songGenres.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!songGenresToDelete.isEmpty()) {
      songGenreRepository.deleteAll(songGenresToDelete);
    }

    for (SongGenre songGenre : songGenres) {
      songGenre.setSongidSongs(song);
      
      if (songGenre.getId() != null && 
          existingSongGenres.stream()
            .anyMatch(e -> e.getId().equals(songGenre.getId()))) {
          songGenreRepository.save(songGenre);
      } else {
          songGenre.setId(null);
          songGenreRepository.save(songGenre);
      }
    }
    List<UpNextQueue> existingUpNextQueues = upNextQueueRepository.findBySongidSongs(song);

    List<UpNextQueue> upNextQueuesToDelete = existingUpNextQueues.stream()
      .filter(existing -> upNextQueues.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!upNextQueuesToDelete.isEmpty()) {
      upNextQueueRepository.deleteAll(upNextQueuesToDelete);
    }

    for (UpNextQueue upNextQueue : upNextQueues) {
      upNextQueue.setSongidSongs(song);
      
      if (upNextQueue.getId() != null && 
          existingUpNextQueues.stream()
            .anyMatch(e -> e.getId().equals(upNextQueue.getId()))) {
          upNextQueueRepository.save(upNextQueue);
      } else {
          upNextQueue.setId(null);
          upNextQueueRepository.save(upNextQueue);
      }
    }
    
    
    return song;
  } catch (ResourceNotFoundException ex) {
    throw ex;
  } catch (Exception ex) {
    throw new InternalServerErrorException("Error during atomic update of Song and its details.", ex);
  }
}
  
  @Override
  @Transactional
  public void deleteFullSong(Long id) {
  try {
  Song song = songRepository.findById(id)
  .orElseThrow(() -> new ResourceNotFoundException(
  "Song not found with id: " + id));
        
        List<EditorialPlaylistSong> editorialPlaylistSongsToDelete = editorialPlaylistSongRepository.findBySongidSongs(song);
        
        if (!editorialPlaylistSongsToDelete.isEmpty()) {
            editorialPlaylistSongRepository.deleteAll(editorialPlaylistSongsToDelete);
        }
        List<ListeningHistory> listeningHistorysToDelete = listeningHistoryRepository.findBySongidSongs(song);
        
        if (!listeningHistorysToDelete.isEmpty()) {
            listeningHistoryRepository.deleteAll(listeningHistorysToDelete);
        }
        List<PlaylistSong> playlistSongsToDelete = playlistSongRepository.findBySongidSongs(song);
        
        if (!playlistSongsToDelete.isEmpty()) {
            playlistSongRepository.deleteAll(playlistSongsToDelete);
        }
        List<SongGenre> songGenresToDelete = songGenreRepository.findBySongidSongs(song);
        
        if (!songGenresToDelete.isEmpty()) {
            songGenreRepository.deleteAll(songGenresToDelete);
        }
        List<UpNextQueue> upNextQueuesToDelete = upNextQueueRepository.findBySongidSongs(song);
        
        if (!upNextQueuesToDelete.isEmpty()) {
            upNextQueueRepository.deleteAll(upNextQueuesToDelete);
        }
        
        
        songRepository.delete(song);
        
    } catch (ResourceNotFoundException ex) {
        throw ex;
    } catch (Exception ex) {
        throw new InternalServerErrorException(
            "Error during atomic deletion of Song and its details.", ex);
}
}
  

}
