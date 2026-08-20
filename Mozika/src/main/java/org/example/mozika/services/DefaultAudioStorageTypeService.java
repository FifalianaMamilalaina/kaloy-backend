package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.AudioStorageType;
import org.example.mozika.models.dto.AudioStorageTypeSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.AudioStorageTypeRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.AudioStorageTypeService;
import org.example.mozika.specification.AudioStorageTypeSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultAudioStorageTypeService implements AudioStorageTypeService {
	private final AudioStorageTypeRepository audioStorageTypeRepository;

	public DefaultAudioStorageTypeService(AudioStorageTypeRepository audioStorageTypeRepository) {
	   this.audioStorageTypeRepository = audioStorageTypeRepository;
	}

	@Override
	public String exportAudioStorageTypeToCSV(List<AudioStorageType> audioStorageType) {
	   return ExportUtils.generateCsv(audioStorageType);
	}  

	@Override
	public Page<AudioStorageType> getAllAudioStorageType(Pageable pageable) {
	    try {
	        return audioStorageTypeRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving audio storage type", ex);
	    }
	}

	@Override
	public Page<AudioStorageType> getAllAudioStorageType(Pageable pageable, AudioStorageTypeSearch object) {
	    try {
	        Specification<AudioStorageType> spec=AudioStorageTypeSpecification.filter(object);
	        return audioStorageTypeRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving audio storage type", ex);
	    }
	}

	@Override
	public AudioStorageType getAudioStorageTypeById(Long id) {
	    Optional<AudioStorageType> audioStorageType = audioStorageTypeRepository.findById(id);
	    if (audioStorageType.isPresent()) {
	        return audioStorageType.get();
	    } else {
	        throw new ResourceNotFoundException("AudioStorageType not found with id : " + id);
	    }
	}

	@Override
	public AudioStorageType createAudioStorageType(AudioStorageType audioStorageType) {
	    try {
	        return audioStorageTypeRepository.save(audioStorageType);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating audio storage type", ex);
	    }
	}

	@Override
	public AudioStorageType updateAudioStorageType(Long id, AudioStorageType audioStorageType) {
	    Optional<AudioStorageType> existingAudioStorageType = audioStorageTypeRepository.findById(id);
	    if (existingAudioStorageType.isPresent()) {
	        audioStorageType.setId(id);
	        try {
	            return audioStorageTypeRepository.save(audioStorageType);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating audio storage type", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("AudioStorageType not found with id : " + id);
	    }
	}

	@Override
	public void deleteAudioStorageType(Long id) {
	    try {
	        audioStorageTypeRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting audio storage type", ex);
	    }
	}



}
