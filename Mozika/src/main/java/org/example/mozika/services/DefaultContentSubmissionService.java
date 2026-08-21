package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.ContentSubmission;
import org.example.mozika.models.dto.ContentSubmissionSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.ContentSubmissionRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.ContentSubmissionService;
import org.example.mozika.specification.ContentSubmissionSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultContentSubmissionService implements ContentSubmissionService {
	private final ContentSubmissionRepository contentSubmissionRepository;

	public DefaultContentSubmissionService(ContentSubmissionRepository contentSubmissionRepository) {
	   this.contentSubmissionRepository = contentSubmissionRepository;
	}

	@Override
	public String exportContentSubmissionToCSV(List<ContentSubmission> contentSubmission) {
	   return ExportUtils.generateCsv(contentSubmission);
	}  

	@Override
	public Page<ContentSubmission> getAllContentSubmission(Pageable pageable) {
	    try {
	        return contentSubmissionRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving content submission", ex);
	    }
	}

	@Override
	public Page<ContentSubmission> getAllContentSubmission(Pageable pageable, ContentSubmissionSearch object) {
	    try {
	        Specification<ContentSubmission> spec=ContentSubmissionSpecification.filter(object);
	        return contentSubmissionRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving content submission", ex);
	    }
	}

	@Override
	public ContentSubmission getContentSubmissionById(Long id) {
	    Optional<ContentSubmission> contentSubmission = contentSubmissionRepository.findById(id);
	    if (contentSubmission.isPresent()) {
	        return contentSubmission.get();
	    } else {
	        throw new ResourceNotFoundException("ContentSubmission not found with id : " + id);
	    }
	}

	@Override
	public ContentSubmission createContentSubmission(ContentSubmission contentSubmission) {
	    try {
	        return contentSubmissionRepository.save(contentSubmission);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating content submission", ex);
	    }
	}

	@Override
	public ContentSubmission updateContentSubmission(Long id, ContentSubmission contentSubmission) {
	    Optional<ContentSubmission> existingContentSubmission = contentSubmissionRepository.findById(id);
	    if (existingContentSubmission.isPresent()) {
	        contentSubmission.setId(id);
	        try {
	            return contentSubmissionRepository.save(contentSubmission);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating content submission", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("ContentSubmission not found with id : " + id);
	    }
	}

	@Override
	public void deleteContentSubmission(Long id) {
	    try {
	        contentSubmissionRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting content submission", ex);
	    }
	}



}
