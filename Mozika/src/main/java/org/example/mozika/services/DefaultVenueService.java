package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.Venue;
import org.example.mozika.models.dto.VenueSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.VenueRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.VenueService;
import org.example.mozika.specification.VenueSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;

import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import org.example.mozika.repositories.ConcertRepository;
import org.example.mozika.models.Concert;


@Service
public class DefaultVenueService implements VenueService {
	private final VenueRepository venueRepository;
private final ConcertRepository concertRepository;


	public DefaultVenueService(VenueRepository venueRepository, ConcertRepository concertRepository) {
	   this.venueRepository = venueRepository;
this.concertRepository = concertRepository;

	}

	@Override
	public String exportVenueToCSV(List<Venue> venue) {
	   return ExportUtils.generateCsv(venue);
	}  

	@Override
	public Page<Venue> getAllVenue(Pageable pageable) {
	    try {
	        return venueRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving venue", ex);
	    }
	}

	@Override
	public Page<Venue> getAllVenue(Pageable pageable, VenueSearch object) {
	    try {
	        Specification<Venue> spec=VenueSpecification.filter(object);
	        return venueRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving venue", ex);
	    }
	}

	@Override
	public Venue getVenueById(Long id) {
	    Optional<Venue> venue = venueRepository.findById(id);
	    if (venue.isPresent()) {
	        return venue.get();
	    } else {
	        throw new ResourceNotFoundException("Venue not found with id : " + id);
	    }
	}

	@Override
	public Venue createVenue(Venue venue) {
	    try {
	        return venueRepository.save(venue);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating venue", ex);
	    }
	}

	@Override
	public Venue updateVenue(Long id, Venue venue) {
	    Optional<Venue> existingVenue = venueRepository.findById(id);
	    if (existingVenue.isPresent()) {
	        venue.setId(id);
	        try {
	            return venueRepository.save(venue);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating venue", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("Venue not found with id : " + id);
	    }
	}

	@Override
	public void deleteVenue(Long id) {
	    try {
	        venueRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting venue", ex);
	    }
	}


@Override
@Transactional
public Venue createFullVenue(Venue venue, List<Concert> concerts) {
  try {
    venue = venueRepository.save(venue);
    
    for (Concert concert : concerts) {
      concert.setVenueidVenues(venue);
      concert.setId(null);
    }
    
    concertRepository.saveAll(concerts);
    
    
    return venue;
  } catch (Exception ex) {
    throw new InternalServerErrorException(
    "Error during atomic creation of Venue and its details.", ex);
  }
}
  
@Override
@Transactional
public Venue updateFullVenue(Long id, Venue venue, List<Concert> concerts) {
  try {
    if (!venueRepository.existsById(id)) {
      throw new ResourceNotFoundException("Venue not found with id: " + id);
    }

    venue.setId(id);
    venue = venueRepository.save(venue);

    List<Concert> existingConcerts = concertRepository.findByVenueidVenues(venue);

    List<Concert> concertsToDelete = existingConcerts.stream()
      .filter(existing -> concerts.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!concertsToDelete.isEmpty()) {
      concertRepository.deleteAll(concertsToDelete);
    }

    for (Concert concert : concerts) {
      concert.setVenueidVenues(venue);
      
      if (concert.getId() != null && 
          existingConcerts.stream()
            .anyMatch(e -> e.getId().equals(concert.getId()))) {
          concertRepository.save(concert);
      } else {
          concert.setId(null);
          concertRepository.save(concert);
      }
    }
    
    
    return venue;
  } catch (ResourceNotFoundException ex) {
    throw ex;
  } catch (Exception ex) {
    throw new InternalServerErrorException("Error during atomic update of Venue and its details.", ex);
  }
}
  
  @Override
  @Transactional
  public void deleteFullVenue(Long id) {
  try {
  Venue venue = venueRepository.findById(id)
  .orElseThrow(() -> new ResourceNotFoundException(
  "Venue not found with id: " + id));
        
        List<Concert> concertsToDelete = concertRepository.findByVenueidVenues(venue);
        
        if (!concertsToDelete.isEmpty()) {
            concertRepository.deleteAll(concertsToDelete);
        }
        
        
        venueRepository.delete(venue);
        
    } catch (ResourceNotFoundException ex) {
        throw ex;
    } catch (Exception ex) {
        throw new InternalServerErrorException(
            "Error during atomic deletion of Venue and its details.", ex);
}
}
  

}
