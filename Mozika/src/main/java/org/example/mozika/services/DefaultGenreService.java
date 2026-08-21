package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.Genre;
import org.example.mozika.models.dto.GenreSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.GenreRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.GenreService;
import org.example.mozika.specification.GenreSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;

import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import org.example.mozika.repositories.SongGenreRepository;
import org.example.mozika.models.SongGenre;


@Service
public class DefaultGenreService implements GenreService {
	private final GenreRepository genreRepository;
private final SongGenreRepository songGenreRepository;


	public DefaultGenreService(GenreRepository genreRepository, SongGenreRepository songGenreRepository) {
	   this.genreRepository = genreRepository;
this.songGenreRepository = songGenreRepository;

	}

	@Override
	public String exportGenreToCSV(List<Genre> genre) {
	   return ExportUtils.generateCsv(genre);
	}  

	@Override
	public Page<Genre> getAllGenre(Pageable pageable) {
	    try {
	        return genreRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving genre", ex);
	    }
	}

	@Override
	public Page<Genre> getAllGenre(Pageable pageable, GenreSearch object) {
	    try {
	        Specification<Genre> spec=GenreSpecification.filter(object);
	        return genreRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving genre", ex);
	    }
	}

	@Override
	public Genre getGenreById(Long id) {
	    Optional<Genre> genre = genreRepository.findById(id);
	    if (genre.isPresent()) {
	        return genre.get();
	    } else {
	        throw new ResourceNotFoundException("Genre not found with id : " + id);
	    }
	}

	@Override
	public Genre createGenre(Genre genre) {
	    try {
	        return genreRepository.save(genre);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating genre", ex);
	    }
	}

	@Override
	public Genre updateGenre(Long id, Genre genre) {
	    Optional<Genre> existingGenre = genreRepository.findById(id);
	    if (existingGenre.isPresent()) {
	        genre.setId(id);
	        try {
	            return genreRepository.save(genre);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating genre", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("Genre not found with id : " + id);
	    }
	}

	@Override
	public void deleteGenre(Long id) {
	    try {
	        genreRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting genre", ex);
	    }
	}


@Override
@Transactional
public Genre createFullGenre(Genre genre, List<SongGenre> songGenres) {
  try {
    genre = genreRepository.save(genre);
    
    for (SongGenre songGenre : songGenres) {
      songGenre.setGenreidGenres(genre);
      songGenre.setId(null);
    }
    
    songGenreRepository.saveAll(songGenres);
    
    
    return genre;
  } catch (Exception ex) {
    throw new InternalServerErrorException(
    "Error during atomic creation of Genre and its details.", ex);
  }
}
  
@Override
@Transactional
public Genre updateFullGenre(Long id, Genre genre, List<SongGenre> songGenres) {
  try {
    if (!genreRepository.existsById(id)) {
      throw new ResourceNotFoundException("Genre not found with id: " + id);
    }

    genre.setId(id);
    genre = genreRepository.save(genre);

    List<SongGenre> existingSongGenres = songGenreRepository.findByGenreidGenres(genre);

    List<SongGenre> songGenresToDelete = existingSongGenres.stream()
      .filter(existing -> songGenres.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!songGenresToDelete.isEmpty()) {
      songGenreRepository.deleteAll(songGenresToDelete);
    }

    for (SongGenre songGenre : songGenres) {
      songGenre.setGenreidGenres(genre);
      
      if (songGenre.getId() != null && 
          existingSongGenres.stream()
            .anyMatch(e -> e.getId().equals(songGenre.getId()))) {
          songGenreRepository.save(songGenre);
      } else {
          songGenre.setId(null);
          songGenreRepository.save(songGenre);
      }
    }
    
    
    return genre;
  } catch (ResourceNotFoundException ex) {
    throw ex;
  } catch (Exception ex) {
    throw new InternalServerErrorException("Error during atomic update of Genre and its details.", ex);
  }
}
  
  @Override
  @Transactional
  public void deleteFullGenre(Long id) {
  try {
  Genre genre = genreRepository.findById(id)
  .orElseThrow(() -> new ResourceNotFoundException(
  "Genre not found with id: " + id));
        
        List<SongGenre> songGenresToDelete = songGenreRepository.findByGenreidGenres(genre);
        
        if (!songGenresToDelete.isEmpty()) {
            songGenreRepository.deleteAll(songGenresToDelete);
        }
        
        
        genreRepository.delete(genre);
        
    } catch (ResourceNotFoundException ex) {
        throw ex;
    } catch (Exception ex) {
        throw new InternalServerErrorException(
            "Error during atomic deletion of Genre and its details.", ex);
}
}
  

}
