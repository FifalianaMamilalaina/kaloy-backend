package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.SubmissionStatuse;
import org.example.mozika.models.dto.SubmissionStatuseSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.SubmissionStatuseRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.SubmissionStatuseService;
import org.example.mozika.specification.SubmissionStatuseSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultSubmissionStatuseService implements SubmissionStatuseService {
	private final SubmissionStatuseRepository submissionStatuseRepository;

	public DefaultSubmissionStatuseService(SubmissionStatuseRepository submissionStatuseRepository) {
	   this.submissionStatuseRepository = submissionStatuseRepository;
	}

	@Override
	public String exportSubmissionStatuseToCSV(List<SubmissionStatuse> submissionStatuse) {
	   return ExportUtils.generateCsv(submissionStatuse);
	}  

	@Override
	public Page<SubmissionStatuse> getAllSubmissionStatuse(Pageable pageable) {
	    try {
	        return submissionStatuseRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving submission statuse", ex);
	    }
	}

	@Override
	public Page<SubmissionStatuse> getAllSubmissionStatuse(Pageable pageable, SubmissionStatuseSearch object) {
	    try {
	        Specification<SubmissionStatuse> spec=SubmissionStatuseSpecification.filter(object);
	        return submissionStatuseRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving submission statuse", ex);
	    }
	}

	@Override
	public SubmissionStatuse getSubmissionStatuseById(Long id) {
	    Optional<SubmissionStatuse> submissionStatuse = submissionStatuseRepository.findById(id);
	    if (submissionStatuse.isPresent()) {
	        return submissionStatuse.get();
	    } else {
	        throw new ResourceNotFoundException("SubmissionStatuse not found with id : " + id);
	    }
	}

	@Override
	public SubmissionStatuse createSubmissionStatuse(SubmissionStatuse submissionStatuse) {
	    try {
	        return submissionStatuseRepository.save(submissionStatuse);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating submission statuse", ex);
	    }
	}

	@Override
	public SubmissionStatuse updateSubmissionStatuse(Long id, SubmissionStatuse submissionStatuse) {
	    Optional<SubmissionStatuse> existingSubmissionStatuse = submissionStatuseRepository.findById(id);
	    if (existingSubmissionStatuse.isPresent()) {
	        submissionStatuse.setId(id);
	        try {
	            return submissionStatuseRepository.save(submissionStatuse);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating submission statuse", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("SubmissionStatuse not found with id : " + id);
	    }
	}

	@Override
	public void deleteSubmissionStatuse(Long id) {
	    try {
	        submissionStatuseRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting submission statuse", ex);
	    }
	}



}
