package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.EditorialPlaylist;
import org.example.mozika.models.dto.EditorialPlaylistSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.EditorialPlaylistRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.EditorialPlaylistService;
import org.example.mozika.specification.EditorialPlaylistSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultEditorialPlaylistService implements EditorialPlaylistService {
	private final EditorialPlaylistRepository editorialPlaylistRepository;

	public DefaultEditorialPlaylistService(EditorialPlaylistRepository editorialPlaylistRepository) {
	   this.editorialPlaylistRepository = editorialPlaylistRepository;
	}

	@Override
	public String exportEditorialPlaylistToCSV(List<EditorialPlaylist> editorialPlaylist) {
	   return ExportUtils.generateCsv(editorialPlaylist);
	}  

	@Override
	public Page<EditorialPlaylist> getAllEditorialPlaylist(Pageable pageable) {
	    try {
	        return editorialPlaylistRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving editorial playlist", ex);
	    }
	}

	@Override
	public Page<EditorialPlaylist> getAllEditorialPlaylist(Pageable pageable, EditorialPlaylistSearch object) {
	    try {
	        Specification<EditorialPlaylist> spec=EditorialPlaylistSpecification.filter(object);
	        return editorialPlaylistRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving editorial playlist", ex);
	    }
	}

	@Override
	public EditorialPlaylist getEditorialPlaylistById(Long id) {
	    Optional<EditorialPlaylist> editorialPlaylist = editorialPlaylistRepository.findById(id);
	    if (editorialPlaylist.isPresent()) {
	        return editorialPlaylist.get();
	    } else {
	        throw new ResourceNotFoundException("EditorialPlaylist not found with id : " + id);
	    }
	}

	@Override
	public EditorialPlaylist createEditorialPlaylist(EditorialPlaylist editorialPlaylist) {
	    try {
	        return editorialPlaylistRepository.save(editorialPlaylist);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating editorial playlist", ex);
	    }
	}

	@Override
	public EditorialPlaylist updateEditorialPlaylist(Long id, EditorialPlaylist editorialPlaylist) {
	    Optional<EditorialPlaylist> existingEditorialPlaylist = editorialPlaylistRepository.findById(id);
	    if (existingEditorialPlaylist.isPresent()) {
	        editorialPlaylist.setId(id);
	        try {
	            return editorialPlaylistRepository.save(editorialPlaylist);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating editorial playlist", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("EditorialPlaylist not found with id : " + id);
	    }
	}

	@Override
	public void deleteEditorialPlaylist(Long id) {
	    try {
	        editorialPlaylistRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting editorial playlist", ex);
	    }
	}



}
