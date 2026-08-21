package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.UpNextQueue;
import org.example.mozika.models.dto.UpNextQueueSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.UpNextQueueRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.UpNextQueueService;
import org.example.mozika.specification.UpNextQueueSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultUpNextQueueService implements UpNextQueueService {
	private final UpNextQueueRepository upNextQueueRepository;

	public DefaultUpNextQueueService(UpNextQueueRepository upNextQueueRepository) {
	   this.upNextQueueRepository = upNextQueueRepository;
	}

	@Override
	public String exportUpNextQueueToCSV(List<UpNextQueue> upNextQueue) {
	   return ExportUtils.generateCsv(upNextQueue);
	}  

	@Override
	public Page<UpNextQueue> getAllUpNextQueue(Pageable pageable) {
	    try {
	        return upNextQueueRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving up next queue", ex);
	    }
	}

	@Override
	public Page<UpNextQueue> getAllUpNextQueue(Pageable pageable, UpNextQueueSearch object) {
	    try {
	        Specification<UpNextQueue> spec=UpNextQueueSpecification.filter(object);
	        return upNextQueueRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving up next queue", ex);
	    }
	}

	@Override
	public UpNextQueue getUpNextQueueById(Long id) {
	    Optional<UpNextQueue> upNextQueue = upNextQueueRepository.findById(id);
	    if (upNextQueue.isPresent()) {
	        return upNextQueue.get();
	    } else {
	        throw new ResourceNotFoundException("UpNextQueue not found with id : " + id);
	    }
	}

	@Override
	public UpNextQueue createUpNextQueue(UpNextQueue upNextQueue) {
	    try {
	        return upNextQueueRepository.save(upNextQueue);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating up next queue", ex);
	    }
	}

	@Override
	public UpNextQueue updateUpNextQueue(Long id, UpNextQueue upNextQueue) {
	    Optional<UpNextQueue> existingUpNextQueue = upNextQueueRepository.findById(id);
	    if (existingUpNextQueue.isPresent()) {
	        upNextQueue.setId(id);
	        try {
	            return upNextQueueRepository.save(upNextQueue);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating up next queue", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("UpNextQueue not found with id : " + id);
	    }
	}

	@Override
	public void deleteUpNextQueue(Long id) {
	    try {
	        upNextQueueRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting up next queue", ex);
	    }
	}



}
