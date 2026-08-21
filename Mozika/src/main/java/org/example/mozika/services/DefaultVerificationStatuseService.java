package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.VerificationStatuse;
import org.example.mozika.models.dto.VerificationStatuseSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.VerificationStatuseRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.VerificationStatuseService;
import org.example.mozika.specification.VerificationStatuseSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultVerificationStatuseService implements VerificationStatuseService {
	private final VerificationStatuseRepository verificationStatuseRepository;

	public DefaultVerificationStatuseService(VerificationStatuseRepository verificationStatuseRepository) {
	   this.verificationStatuseRepository = verificationStatuseRepository;
	}

	@Override
	public String exportVerificationStatuseToCSV(List<VerificationStatuse> verificationStatuse) {
	   return ExportUtils.generateCsv(verificationStatuse);
	}  

	@Override
	public Page<VerificationStatuse> getAllVerificationStatuse(Pageable pageable) {
	    try {
	        return verificationStatuseRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving verification statuse", ex);
	    }
	}

	@Override
	public Page<VerificationStatuse> getAllVerificationStatuse(Pageable pageable, VerificationStatuseSearch object) {
	    try {
	        Specification<VerificationStatuse> spec=VerificationStatuseSpecification.filter(object);
	        return verificationStatuseRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving verification statuse", ex);
	    }
	}

	@Override
	public VerificationStatuse getVerificationStatuseById(Long id) {
	    Optional<VerificationStatuse> verificationStatuse = verificationStatuseRepository.findById(id);
	    if (verificationStatuse.isPresent()) {
	        return verificationStatuse.get();
	    } else {
	        throw new ResourceNotFoundException("VerificationStatuse not found with id : " + id);
	    }
	}

	@Override
	public VerificationStatuse createVerificationStatuse(VerificationStatuse verificationStatuse) {
	    try {
	        return verificationStatuseRepository.save(verificationStatuse);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating verification statuse", ex);
	    }
	}

	@Override
	public VerificationStatuse updateVerificationStatuse(Long id, VerificationStatuse verificationStatuse) {
	    Optional<VerificationStatuse> existingVerificationStatuse = verificationStatuseRepository.findById(id);
	    if (existingVerificationStatuse.isPresent()) {
	        verificationStatuse.setId(id);
	        try {
	            return verificationStatuseRepository.save(verificationStatuse);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating verification statuse", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("VerificationStatuse not found with id : " + id);
	    }
	}

	@Override
	public void deleteVerificationStatuse(Long id) {
	    try {
	        verificationStatuseRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting verification statuse", ex);
	    }
	}



}
