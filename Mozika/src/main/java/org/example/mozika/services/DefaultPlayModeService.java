package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.PlayMode;
import org.example.mozika.models.dto.PlayModeSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.PlayModeRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.PlayModeService;
import org.example.mozika.specification.PlayModeSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultPlayModeService implements PlayModeService {
	private final PlayModeRepository playModeRepository;

	public DefaultPlayModeService(PlayModeRepository playModeRepository) {
	   this.playModeRepository = playModeRepository;
	}

	@Override
	public String exportPlayModeToCSV(List<PlayMode> playMode) {
	   return ExportUtils.generateCsv(playMode);
	}  

	@Override
	public Page<PlayMode> getAllPlayMode(Pageable pageable) {
	    try {
	        return playModeRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving play mode", ex);
	    }
	}

	@Override
	public Page<PlayMode> getAllPlayMode(Pageable pageable, PlayModeSearch object) {
	    try {
	        Specification<PlayMode> spec=PlayModeSpecification.filter(object);
	        return playModeRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving play mode", ex);
	    }
	}

	@Override
	public PlayMode getPlayModeById(Long id) {
	    Optional<PlayMode> playMode = playModeRepository.findById(id);
	    if (playMode.isPresent()) {
	        return playMode.get();
	    } else {
	        throw new ResourceNotFoundException("PlayMode not found with id : " + id);
	    }
	}

	@Override
	public PlayMode createPlayMode(PlayMode playMode) {
	    try {
	        return playModeRepository.save(playMode);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating play mode", ex);
	    }
	}

	@Override
	public PlayMode updatePlayMode(Long id, PlayMode playMode) {
	    Optional<PlayMode> existingPlayMode = playModeRepository.findById(id);
	    if (existingPlayMode.isPresent()) {
	        playMode.setId(id);
	        try {
	            return playModeRepository.save(playMode);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating play mode", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("PlayMode not found with id : " + id);
	    }
	}

	@Override
	public void deletePlayMode(Long id) {
	    try {
	        playModeRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting play mode", ex);
	    }
	}



}
