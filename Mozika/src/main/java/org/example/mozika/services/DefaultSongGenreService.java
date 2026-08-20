package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.SongGenre;
import org.example.mozika.models.dto.SongGenreSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.SongGenreRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.SongGenreService;
import org.example.mozika.specification.SongGenreSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultSongGenreService implements SongGenreService {
	private final SongGenreRepository songGenreRepository;

	public DefaultSongGenreService(SongGenreRepository songGenreRepository) {
	   this.songGenreRepository = songGenreRepository;
	}

	@Override
	public String exportSongGenreToCSV(List<SongGenre> songGenre) {
	   return ExportUtils.generateCsv(songGenre);
	}  

	@Override
	public Page<SongGenre> getAllSongGenre(Pageable pageable) {
	    try {
	        return songGenreRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving song genre", ex);
	    }
	}

	@Override
	public Page<SongGenre> getAllSongGenre(Pageable pageable, SongGenreSearch object) {
	    try {
	        Specification<SongGenre> spec=SongGenreSpecification.filter(object);
	        return songGenreRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving song genre", ex);
	    }
	}

	@Override
	public SongGenre getSongGenreById(Long id) {
	    Optional<SongGenre> songGenre = songGenreRepository.findById(id);
	    if (songGenre.isPresent()) {
	        return songGenre.get();
	    } else {
	        throw new ResourceNotFoundException("SongGenre not found with id : " + id);
	    }
	}

	@Override
	public SongGenre createSongGenre(SongGenre songGenre) {
	    try {
	        return songGenreRepository.save(songGenre);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating song genre", ex);
	    }
	}

	@Override
	public SongGenre updateSongGenre(Long id, SongGenre songGenre) {
	    Optional<SongGenre> existingSongGenre = songGenreRepository.findById(id);
	    if (existingSongGenre.isPresent()) {
	        songGenre.setId(id);
	        try {
	            return songGenreRepository.save(songGenre);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating song genre", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("SongGenre not found with id : " + id);
	    }
	}

	@Override
	public void deleteSongGenre(Long id) {
	    try {
	        songGenreRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting song genre", ex);
	    }
	}



}
