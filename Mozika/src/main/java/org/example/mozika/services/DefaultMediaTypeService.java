package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.MediaType;
import org.example.mozika.models.dto.MediaTypeSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.MediaTypeRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.MediaTypeService;
import org.example.mozika.specification.MediaTypeSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultMediaTypeService implements MediaTypeService {
	private final MediaTypeRepository mediaTypeRepository;

	public DefaultMediaTypeService(MediaTypeRepository mediaTypeRepository) {
	   this.mediaTypeRepository = mediaTypeRepository;
	}

	@Override
	public String exportMediaTypeToCSV(List<MediaType> mediaType) {
	   return ExportUtils.generateCsv(mediaType);
	}  

	@Override
	public Page<MediaType> getAllMediaType(Pageable pageable) {
	    try {
	        return mediaTypeRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving media type", ex);
	    }
	}

	@Override
	public Page<MediaType> getAllMediaType(Pageable pageable, MediaTypeSearch object) {
	    try {
	        Specification<MediaType> spec=MediaTypeSpecification.filter(object);
	        return mediaTypeRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving media type", ex);
	    }
	}

	@Override
	public MediaType getMediaTypeById(Long id) {
	    Optional<MediaType> mediaType = mediaTypeRepository.findById(id);
	    if (mediaType.isPresent()) {
	        return mediaType.get();
	    } else {
	        throw new ResourceNotFoundException("MediaType not found with id : " + id);
	    }
	}

	@Override
	public MediaType createMediaType(MediaType mediaType) {
	    try {
	        return mediaTypeRepository.save(mediaType);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating media type", ex);
	    }
	}

	@Override
	public MediaType updateMediaType(Long id, MediaType mediaType) {
	    Optional<MediaType> existingMediaType = mediaTypeRepository.findById(id);
	    if (existingMediaType.isPresent()) {
	        mediaType.setId(id);
	        try {
	            return mediaTypeRepository.save(mediaType);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating media type", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("MediaType not found with id : " + id);
	    }
	}

	@Override
	public void deleteMediaType(Long id) {
	    try {
	        mediaTypeRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting media type", ex);
	    }
	}



}
