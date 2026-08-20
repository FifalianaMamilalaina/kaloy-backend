package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.EventModerationStatuse;
import org.example.mozika.models.dto.EventModerationStatuseSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.EventModerationStatuseRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.EventModerationStatuseService;
import org.example.mozika.specification.EventModerationStatuseSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultEventModerationStatuseService implements EventModerationStatuseService {
	private final EventModerationStatuseRepository eventModerationStatuseRepository;

	public DefaultEventModerationStatuseService(EventModerationStatuseRepository eventModerationStatuseRepository) {
	   this.eventModerationStatuseRepository = eventModerationStatuseRepository;
	}

	@Override
	public String exportEventModerationStatuseToCSV(List<EventModerationStatuse> eventModerationStatuse) {
	   return ExportUtils.generateCsv(eventModerationStatuse);
	}  

	@Override
	public Page<EventModerationStatuse> getAllEventModerationStatuse(Pageable pageable) {
	    try {
	        return eventModerationStatuseRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving event moderation statuse", ex);
	    }
	}

	@Override
	public Page<EventModerationStatuse> getAllEventModerationStatuse(Pageable pageable, EventModerationStatuseSearch object) {
	    try {
	        Specification<EventModerationStatuse> spec=EventModerationStatuseSpecification.filter(object);
	        return eventModerationStatuseRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving event moderation statuse", ex);
	    }
	}

	@Override
	public EventModerationStatuse getEventModerationStatuseById(Long id) {
	    Optional<EventModerationStatuse> eventModerationStatuse = eventModerationStatuseRepository.findById(id);
	    if (eventModerationStatuse.isPresent()) {
	        return eventModerationStatuse.get();
	    } else {
	        throw new ResourceNotFoundException("EventModerationStatuse not found with id : " + id);
	    }
	}

	@Override
	public EventModerationStatuse createEventModerationStatuse(EventModerationStatuse eventModerationStatuse) {
	    try {
	        return eventModerationStatuseRepository.save(eventModerationStatuse);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating event moderation statuse", ex);
	    }
	}

	@Override
	public EventModerationStatuse updateEventModerationStatuse(Long id, EventModerationStatuse eventModerationStatuse) {
	    Optional<EventModerationStatuse> existingEventModerationStatuse = eventModerationStatuseRepository.findById(id);
	    if (existingEventModerationStatuse.isPresent()) {
	        eventModerationStatuse.setId(id);
	        try {
	            return eventModerationStatuseRepository.save(eventModerationStatuse);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating event moderation statuse", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("EventModerationStatuse not found with id : " + id);
	    }
	}

	@Override
	public void deleteEventModerationStatuse(Long id) {
	    try {
	        eventModerationStatuseRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting event moderation statuse", ex);
	    }
	}



}
