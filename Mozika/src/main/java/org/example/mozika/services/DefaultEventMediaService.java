package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.EventMedia;
import org.example.mozika.models.dto.EventMediaSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.EventMediaRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.EventMediaService;
import org.example.mozika.specification.EventMediaSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultEventMediaService implements EventMediaService {
	private final EventMediaRepository eventMediaRepository;

	public DefaultEventMediaService(EventMediaRepository eventMediaRepository) {
	   this.eventMediaRepository = eventMediaRepository;
	}

	@Override
	public String exportEventMediaToCSV(List<EventMedia> eventMedia) {
	   return ExportUtils.generateCsv(eventMedia);
	}  

	@Override
	public Page<EventMedia> getAllEventMedia(Pageable pageable) {
	    try {
	        return eventMediaRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving event media", ex);
	    }
	}

	@Override
	public Page<EventMedia> getAllEventMedia(Pageable pageable, EventMediaSearch object) {
	    try {
	        Specification<EventMedia> spec=EventMediaSpecification.filter(object);
	        return eventMediaRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving event media", ex);
	    }
	}

	@Override
	public EventMedia getEventMediaById(Long id) {
	    Optional<EventMedia> eventMedia = eventMediaRepository.findById(id);
	    if (eventMedia.isPresent()) {
	        return eventMedia.get();
	    } else {
	        throw new ResourceNotFoundException("EventMedia not found with id : " + id);
	    }
	}

	@Override
	public EventMedia createEventMedia(EventMedia eventMedia) {
	    try {
	        return eventMediaRepository.save(eventMedia);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating event media", ex);
	    }
	}

	@Override
	public EventMedia updateEventMedia(Long id, EventMedia eventMedia) {
	    Optional<EventMedia> existingEventMedia = eventMediaRepository.findById(id);
	    if (existingEventMedia.isPresent()) {
	        eventMedia.setId(id);
	        try {
	            return eventMediaRepository.save(eventMedia);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating event media", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("EventMedia not found with id : " + id);
	    }
	}

	@Override
	public void deleteEventMedia(Long id) {
	    try {
	        eventMediaRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting event media", ex);
	    }
	}



}
