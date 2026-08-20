package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.EditorialPlaylistSong;
import org.example.mozika.models.dto.EditorialPlaylistSongSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.EditorialPlaylistSongRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.EditorialPlaylistSongService;
import org.example.mozika.specification.EditorialPlaylistSongSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultEditorialPlaylistSongService implements EditorialPlaylistSongService {
	private final EditorialPlaylistSongRepository editorialPlaylistSongRepository;

	public DefaultEditorialPlaylistSongService(EditorialPlaylistSongRepository editorialPlaylistSongRepository) {
	   this.editorialPlaylistSongRepository = editorialPlaylistSongRepository;
	}

	@Override
	public String exportEditorialPlaylistSongToCSV(List<EditorialPlaylistSong> editorialPlaylistSong) {
	   return ExportUtils.generateCsv(editorialPlaylistSong);
	}  

	@Override
	public Page<EditorialPlaylistSong> getAllEditorialPlaylistSong(Pageable pageable) {
	    try {
	        return editorialPlaylistSongRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving editorial playlist song", ex);
	    }
	}

	@Override
	public Page<EditorialPlaylistSong> getAllEditorialPlaylistSong(Pageable pageable, EditorialPlaylistSongSearch object) {
	    try {
	        Specification<EditorialPlaylistSong> spec=EditorialPlaylistSongSpecification.filter(object);
	        return editorialPlaylistSongRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving editorial playlist song", ex);
	    }
	}

	@Override
	public EditorialPlaylistSong getEditorialPlaylistSongById(Long id) {
	    Optional<EditorialPlaylistSong> editorialPlaylistSong = editorialPlaylistSongRepository.findById(id);
	    if (editorialPlaylistSong.isPresent()) {
	        return editorialPlaylistSong.get();
	    } else {
	        throw new ResourceNotFoundException("EditorialPlaylistSong not found with id : " + id);
	    }
	}

	@Override
	public EditorialPlaylistSong createEditorialPlaylistSong(EditorialPlaylistSong editorialPlaylistSong) {
	    try {
	        return editorialPlaylistSongRepository.save(editorialPlaylistSong);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating editorial playlist song", ex);
	    }
	}

	@Override
	public EditorialPlaylistSong updateEditorialPlaylistSong(Long id, EditorialPlaylistSong editorialPlaylistSong) {
	    Optional<EditorialPlaylistSong> existingEditorialPlaylistSong = editorialPlaylistSongRepository.findById(id);
	    if (existingEditorialPlaylistSong.isPresent()) {
	        editorialPlaylistSong.setId(id);
	        try {
	            return editorialPlaylistSongRepository.save(editorialPlaylistSong);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating editorial playlist song", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("EditorialPlaylistSong not found with id : " + id);
	    }
	}

	@Override
	public void deleteEditorialPlaylistSong(Long id) {
	    try {
	        editorialPlaylistSongRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting editorial playlist song", ex);
	    }
	}



}
