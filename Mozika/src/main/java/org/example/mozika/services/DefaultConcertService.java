package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.Concert;
import org.example.mozika.models.dto.ConcertSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.ConcertRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.ConcertService;
import org.example.mozika.specification.ConcertSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultConcertService implements ConcertService {
	private final ConcertRepository concertRepository;

	public DefaultConcertService(ConcertRepository concertRepository) {
	   this.concertRepository = concertRepository;
	}

	@Override
	public String exportConcertToCSV(List<Concert> concert) {
	   return ExportUtils.generateCsv(concert);
	}  

	@Override
	public Page<Concert> getAllConcert(Pageable pageable) {
	    try {
	        return concertRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving concert", ex);
	    }
	}

	@Override
	public Page<Concert> getAllConcert(Pageable pageable, ConcertSearch object) {
	    try {
	        Specification<Concert> spec=ConcertSpecification.filter(object);
	        return concertRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving concert", ex);
	    }
	}

	@Override
	public Concert getConcertById(Long id) {
	    Optional<Concert> concert = concertRepository.findById(id);
	    if (concert.isPresent()) {
	        return concert.get();
	    } else {
	        throw new ResourceNotFoundException("Concert not found with id : " + id);
	    }
	}

	@Override
	public Concert createConcert(Concert concert) {
	    try {
	        return concertRepository.save(concert);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating concert", ex);
	    }
	}

	@Override
	public Concert updateConcert(Long id, Concert concert) {
	    Optional<Concert> existingConcert = concertRepository.findById(id);
	    if (existingConcert.isPresent()) {
	        concert.setId(id);
	        try {
	            return concertRepository.save(concert);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating concert", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("Concert not found with id : " + id);
	    }
	}

	@Override
	public void deleteConcert(Long id) {
	    try {
	        concertRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting concert", ex);
	    }
	}



}
