package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.Event;
import org.example.mozika.models.dto.EventSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.EventRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.EventService;
import org.example.mozika.specification.EventSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;

import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import org.example.mozika.repositories.ConcertRepository;
import org.example.mozika.models.Concert;
import org.example.mozika.repositories.EventMediaRepository;
import org.example.mozika.models.EventMedia;


@Service
public class DefaultEventService implements EventService {
	private final EventRepository eventRepository;
private final ConcertRepository concertRepository;
private final EventMediaRepository eventMediaRepository;


	public DefaultEventService(EventRepository eventRepository, ConcertRepository concertRepository, EventMediaRepository eventMediaRepository) {
	   this.eventRepository = eventRepository;
this.concertRepository = concertRepository;
this.eventMediaRepository = eventMediaRepository;

	}

	@Override
	public String exportEventToCSV(List<Event> event) {
	   return ExportUtils.generateCsv(event);
	}  

	@Override
	public Page<Event> getAllEvent(Pageable pageable) {
	    try {
	        return eventRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving event", ex);
	    }
	}

	@Override
	public Page<Event> getAllEvent(Pageable pageable, EventSearch object) {
	    try {
	        Specification<Event> spec=EventSpecification.filter(object);
	        return eventRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving event", ex);
	    }
	}

	@Override
	public Event getEventById(Long id) {
	    Optional<Event> event = eventRepository.findById(id);
	    if (event.isPresent()) {
	        return event.get();
	    } else {
	        throw new ResourceNotFoundException("Event not found with id : " + id);
	    }
	}

	@Override
	public Event createEvent(Event event) {
	    try {
	        return eventRepository.save(event);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating event", ex);
	    }
	}

	@Override
	public Event updateEvent(Long id, Event event) {
	    Optional<Event> existingEvent = eventRepository.findById(id);
	    if (existingEvent.isPresent()) {
	        event.setId(id);
	        try {
	            return eventRepository.save(event);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating event", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("Event not found with id : " + id);
	    }
	}

	@Override
	public void deleteEvent(Long id) {
	    try {
	        eventRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting event", ex);
	    }
	}


@Override
@Transactional
public Event createFullEvent(Event event, List<Concert> concerts, List<EventMedia> eventMedias) {
  try {
    event = eventRepository.save(event);
    
    for (Concert concert : concerts) {
      concert.setEventidEvents(event);
      concert.setId(null);
    }
    
    concertRepository.saveAll(concerts);
    for (EventMedia eventMedia : eventMedias) {
      eventMedia.setEventidEvents(event);
      eventMedia.setId(null);
    }
    
    eventMediaRepository.saveAll(eventMedias);
    
    
    return event;
  } catch (Exception ex) {
    throw new InternalServerErrorException(
    "Error during atomic creation of Event and its details.", ex);
  }
}
  
@Override
@Transactional
public Event updateFullEvent(Long id, Event event, List<Concert> concerts, List<EventMedia> eventMedias) {
  try {
    if (!eventRepository.existsById(id)) {
      throw new ResourceNotFoundException("Event not found with id: " + id);
    }

    event.setId(id);
    event = eventRepository.save(event);

    List<Concert> existingConcerts = concertRepository.findByEventidEvents(event);

    List<Concert> concertsToDelete = existingConcerts.stream()
      .filter(existing -> concerts.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!concertsToDelete.isEmpty()) {
      concertRepository.deleteAll(concertsToDelete);
    }

    for (Concert concert : concerts) {
      concert.setEventidEvents(event);
      
      if (concert.getId() != null && 
          existingConcerts.stream()
            .anyMatch(e -> e.getId().equals(concert.getId()))) {
          concertRepository.save(concert);
      } else {
          concert.setId(null);
          concertRepository.save(concert);
      }
    }
    List<EventMedia> existingEventMedias = eventMediaRepository.findByEventidEvents(event);

    List<EventMedia> eventMediasToDelete = existingEventMedias.stream()
      .filter(existing -> eventMedias.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!eventMediasToDelete.isEmpty()) {
      eventMediaRepository.deleteAll(eventMediasToDelete);
    }

    for (EventMedia eventMedia : eventMedias) {
      eventMedia.setEventidEvents(event);
      
      if (eventMedia.getId() != null && 
          existingEventMedias.stream()
            .anyMatch(e -> e.getId().equals(eventMedia.getId()))) {
          eventMediaRepository.save(eventMedia);
      } else {
          eventMedia.setId(null);
          eventMediaRepository.save(eventMedia);
      }
    }
    
    
    return event;
  } catch (ResourceNotFoundException ex) {
    throw ex;
  } catch (Exception ex) {
    throw new InternalServerErrorException("Error during atomic update of Event and its details.", ex);
  }
}
  
  @Override
  @Transactional
  public void deleteFullEvent(Long id) {
  try {
  Event event = eventRepository.findById(id)
  .orElseThrow(() -> new ResourceNotFoundException(
  "Event not found with id: " + id));
        
        List<Concert> concertsToDelete = concertRepository.findByEventidEvents(event);
        
        if (!concertsToDelete.isEmpty()) {
            concertRepository.deleteAll(concertsToDelete);
        }
        List<EventMedia> eventMediasToDelete = eventMediaRepository.findByEventidEvents(event);
        
        if (!eventMediasToDelete.isEmpty()) {
            eventMediaRepository.deleteAll(eventMediasToDelete);
        }
        
        
        eventRepository.delete(event);
        
    } catch (ResourceNotFoundException ex) {
        throw ex;
    } catch (Exception ex) {
        throw new InternalServerErrorException(
            "Error during atomic deletion of Event and its details.", ex);
}
}
  

}
