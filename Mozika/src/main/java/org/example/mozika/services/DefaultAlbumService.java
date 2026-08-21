package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.Album;
import org.example.mozika.models.dto.AlbumSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.AlbumRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.AlbumService;
import org.example.mozika.specification.AlbumSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;

import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import org.example.mozika.repositories.SongRepository;
import org.example.mozika.models.Song;


@Service
public class DefaultAlbumService implements AlbumService {
	private final AlbumRepository albumRepository;
private final SongRepository songRepository;


	public DefaultAlbumService(AlbumRepository albumRepository, SongRepository songRepository) {
	   this.albumRepository = albumRepository;
this.songRepository = songRepository;

	}

	@Override
	public String exportAlbumToCSV(List<Album> album) {
	   return ExportUtils.generateCsv(album);
	}  

	@Override
	public Page<Album> getAllAlbum(Pageable pageable) {
	    try {
	        return albumRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving album", ex);
	    }
	}

	@Override
	public Page<Album> getAllAlbum(Pageable pageable, AlbumSearch object) {
	    try {
	        Specification<Album> spec=AlbumSpecification.filter(object);
	        return albumRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving album", ex);
	    }
	}

	@Override
	public Album getAlbumById(Long id) {
	    Optional<Album> album = albumRepository.findById(id);
	    if (album.isPresent()) {
	        return album.get();
	    } else {
	        throw new ResourceNotFoundException("Album not found with id : " + id);
	    }
	}

	@Override
	public Album createAlbum(Album album) {
	    try {
	        return albumRepository.save(album);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating album", ex);
	    }
	}

	@Override
	public Album updateAlbum(Long id, Album album) {
	    Optional<Album> existingAlbum = albumRepository.findById(id);
	    if (existingAlbum.isPresent()) {
	        album.setId(id);
	        try {
	            return albumRepository.save(album);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating album", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("Album not found with id : " + id);
	    }
	}

	@Override
	public void deleteAlbum(Long id) {
	    try {
	        albumRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting album", ex);
	    }
	}


@Override
@Transactional
public Album createFullAlbum(Album album, List<Song> songs) {
  try {
    album = albumRepository.save(album);
    
    for (Song song : songs) {
      song.setAlbumidAlbums(album);
      song.setId(null);
    }
    
    songRepository.saveAll(songs);
    
    
    return album;
  } catch (Exception ex) {
    throw new InternalServerErrorException(
    "Error during atomic creation of Album and its details.", ex);
  }
}
  
@Override
@Transactional
public Album updateFullAlbum(Long id, Album album, List<Song> songs) {
  try {
    if (!albumRepository.existsById(id)) {
      throw new ResourceNotFoundException("Album not found with id: " + id);
    }

    album.setId(id);
    album = albumRepository.save(album);

    List<Song> existingSongs = songRepository.findByAlbumidAlbums(album);

    List<Song> songsToDelete = existingSongs.stream()
      .filter(existing -> songs.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!songsToDelete.isEmpty()) {
      songRepository.deleteAll(songsToDelete);
    }

    for (Song song : songs) {
      song.setAlbumidAlbums(album);
      
      if (song.getId() != null && 
          existingSongs.stream()
            .anyMatch(e -> e.getId().equals(song.getId()))) {
          songRepository.save(song);
      } else {
          song.setId(null);
          songRepository.save(song);
      }
    }
    
    
    return album;
  } catch (ResourceNotFoundException ex) {
    throw ex;
  } catch (Exception ex) {
    throw new InternalServerErrorException("Error during atomic update of Album and its details.", ex);
  }
}
  
  @Override
  @Transactional
  public void deleteFullAlbum(Long id) {
  try {
  Album album = albumRepository.findById(id)
  .orElseThrow(() -> new ResourceNotFoundException(
  "Album not found with id: " + id));
        
        List<Song> songsToDelete = songRepository.findByAlbumidAlbums(album);
        
        if (!songsToDelete.isEmpty()) {
            songRepository.deleteAll(songsToDelete);
        }
        
        
        albumRepository.delete(album);
        
    } catch (ResourceNotFoundException ex) {
        throw ex;
    } catch (Exception ex) {
        throw new InternalServerErrorException(
            "Error during atomic deletion of Album and its details.", ex);
}
}
  

}
