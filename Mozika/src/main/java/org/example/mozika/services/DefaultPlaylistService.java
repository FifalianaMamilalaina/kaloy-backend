package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.Playlist;
import org.example.mozika.models.dto.PlaylistSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.PlaylistRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.PlaylistService;
import org.example.mozika.specification.PlaylistSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;

import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import org.example.mozika.repositories.DownloadRepository;
import org.example.mozika.models.Download;
import org.example.mozika.repositories.PlaylistSongRepository;
import org.example.mozika.models.PlaylistSong;


@Service
public class DefaultPlaylistService implements PlaylistService {
	private final PlaylistRepository playlistRepository;
private final DownloadRepository downloadRepository;
private final PlaylistSongRepository playlistSongRepository;


	public DefaultPlaylistService(PlaylistRepository playlistRepository, DownloadRepository downloadRepository, PlaylistSongRepository playlistSongRepository) {
	   this.playlistRepository = playlistRepository;
this.downloadRepository = downloadRepository;
this.playlistSongRepository = playlistSongRepository;

	}

	@Override
	public String exportPlaylistToCSV(List<Playlist> playlist) {
	   return ExportUtils.generateCsv(playlist);
	}  

	@Override
	public Page<Playlist> getAllPlaylist(Pageable pageable) {
	    try {
	        return playlistRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving playlist", ex);
	    }
	}

	@Override
	public Page<Playlist> getAllPlaylist(Pageable pageable, PlaylistSearch object) {
	    try {
	        Specification<Playlist> spec=PlaylistSpecification.filter(object);
	        return playlistRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving playlist", ex);
	    }
	}

	@Override
	public Playlist getPlaylistById(Long id) {
	    Optional<Playlist> playlist = playlistRepository.findById(id);
	    if (playlist.isPresent()) {
	        return playlist.get();
	    } else {
	        throw new ResourceNotFoundException("Playlist not found with id : " + id);
	    }
	}

	@Override
	public Playlist createPlaylist(Playlist playlist) {
	    try {
	        return playlistRepository.save(playlist);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating playlist", ex);
	    }
	}

	@Override
	public Playlist updatePlaylist(Long id, Playlist playlist) {
	    Optional<Playlist> existingPlaylist = playlistRepository.findById(id);
	    if (existingPlaylist.isPresent()) {
	        playlist.setId(id);
	        try {
	            return playlistRepository.save(playlist);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating playlist", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("Playlist not found with id : " + id);
	    }
	}

	@Override
	public void deletePlaylist(Long id) {
	    try {
	        playlistRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting playlist", ex);
	    }
	}


@Override
@Transactional
public Playlist createFullPlaylist(Playlist playlist, List<Download> downloads, List<PlaylistSong> playlistSongs) {
  try {
    playlist = playlistRepository.save(playlist);
    
    for (Download download : downloads) {
      download.setPlaylistidPlaylists(playlist);
      download.setId(null);
    }
    
    downloadRepository.saveAll(downloads);
    for (PlaylistSong playlistSong : playlistSongs) {
      playlistSong.setPlaylistidPlaylists(playlist);
      playlistSong.setId(null);
    }
    
    playlistSongRepository.saveAll(playlistSongs);
    
    
    return playlist;
  } catch (Exception ex) {
    throw new InternalServerErrorException(
    "Error during atomic creation of Playlist and its details.", ex);
  }
}
  
@Override
@Transactional
public Playlist updateFullPlaylist(Long id, Playlist playlist, List<Download> downloads, List<PlaylistSong> playlistSongs) {
  try {
    if (!playlistRepository.existsById(id)) {
      throw new ResourceNotFoundException("Playlist not found with id: " + id);
    }

    playlist.setId(id);
    playlist = playlistRepository.save(playlist);

    List<Download> existingDownloads = downloadRepository.findByPlaylistidPlaylists(playlist);

    List<Download> downloadsToDelete = existingDownloads.stream()
      .filter(existing -> downloads.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!downloadsToDelete.isEmpty()) {
      downloadRepository.deleteAll(downloadsToDelete);
    }

    for (Download download : downloads) {
      download.setPlaylistidPlaylists(playlist);
      
      if (download.getId() != null && 
          existingDownloads.stream()
            .anyMatch(e -> e.getId().equals(download.getId()))) {
          downloadRepository.save(download);
      } else {
          download.setId(null);
          downloadRepository.save(download);
      }
    }
    List<PlaylistSong> existingPlaylistSongs = playlistSongRepository.findByPlaylistidPlaylists(playlist);

    List<PlaylistSong> playlistSongsToDelete = existingPlaylistSongs.stream()
      .filter(existing -> playlistSongs.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!playlistSongsToDelete.isEmpty()) {
      playlistSongRepository.deleteAll(playlistSongsToDelete);
    }

    for (PlaylistSong playlistSong : playlistSongs) {
      playlistSong.setPlaylistidPlaylists(playlist);
      
      if (playlistSong.getId() != null && 
          existingPlaylistSongs.stream()
            .anyMatch(e -> e.getId().equals(playlistSong.getId()))) {
          playlistSongRepository.save(playlistSong);
      } else {
          playlistSong.setId(null);
          playlistSongRepository.save(playlistSong);
      }
    }
    
    
    return playlist;
  } catch (ResourceNotFoundException ex) {
    throw ex;
  } catch (Exception ex) {
    throw new InternalServerErrorException("Error during atomic update of Playlist and its details.", ex);
  }
}
  
  @Override
  @Transactional
  public void deleteFullPlaylist(Long id) {
  try {
  Playlist playlist = playlistRepository.findById(id)
  .orElseThrow(() -> new ResourceNotFoundException(
  "Playlist not found with id: " + id));
        
        List<Download> downloadsToDelete = downloadRepository.findByPlaylistidPlaylists(playlist);
        
        if (!downloadsToDelete.isEmpty()) {
            downloadRepository.deleteAll(downloadsToDelete);
        }
        List<PlaylistSong> playlistSongsToDelete = playlistSongRepository.findByPlaylistidPlaylists(playlist);
        
        if (!playlistSongsToDelete.isEmpty()) {
            playlistSongRepository.deleteAll(playlistSongsToDelete);
        }
        
        
        playlistRepository.delete(playlist);
        
    } catch (ResourceNotFoundException ex) {
        throw ex;
    } catch (Exception ex) {
        throw new InternalServerErrorException(
            "Error during atomic deletion of Playlist and its details.", ex);
}
}
  

}
