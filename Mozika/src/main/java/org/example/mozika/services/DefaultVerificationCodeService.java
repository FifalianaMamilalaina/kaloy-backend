package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.VerificationCode;
import org.example.mozika.models.dto.VerificationCodeSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.VerificationCodeRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.VerificationCodeService;
import org.example.mozika.specification.VerificationCodeSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultVerificationCodeService implements VerificationCodeService {
	private final VerificationCodeRepository verificationCodeRepository;

	public DefaultVerificationCodeService(VerificationCodeRepository verificationCodeRepository) {
	   this.verificationCodeRepository = verificationCodeRepository;
	}

	@Override
	public String exportVerificationCodeToCSV(List<VerificationCode> verificationCode) {
	   return ExportUtils.generateCsv(verificationCode);
	}  

	@Override
	public Page<VerificationCode> getAllVerificationCode(Pageable pageable) {
	    try {
	        return verificationCodeRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving verification code", ex);
	    }
	}

	@Override
	public Page<VerificationCode> getAllVerificationCode(Pageable pageable, VerificationCodeSearch object) {
	    try {
	        Specification<VerificationCode> spec=VerificationCodeSpecification.filter(object);
	        return verificationCodeRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving verification code", ex);
	    }
	}

	@Override
	public VerificationCode getVerificationCodeById(Long id) {
	    Optional<VerificationCode> verificationCode = verificationCodeRepository.findById(id);
	    if (verificationCode.isPresent()) {
	        return verificationCode.get();
	    } else {
	        throw new ResourceNotFoundException("VerificationCode not found with id : " + id);
	    }
	}

	@Override
	public VerificationCode createVerificationCode(VerificationCode verificationCode) {
	    try {
	        return verificationCodeRepository.save(verificationCode);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating verification code", ex);
	    }
	}

	@Override
	public VerificationCode updateVerificationCode(Long id, VerificationCode verificationCode) {
	    Optional<VerificationCode> existingVerificationCode = verificationCodeRepository.findById(id);
	    if (existingVerificationCode.isPresent()) {
	        verificationCode.setId(id);
	        try {
	            return verificationCodeRepository.save(verificationCode);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating verification code", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("VerificationCode not found with id : " + id);
	    }
	}

	@Override
	public void deleteVerificationCode(Long id) {
	    try {
	        verificationCodeRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting verification code", ex);
	    }
	}



}
