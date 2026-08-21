package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.VerificationChannel;
import org.example.mozika.models.dto.VerificationChannelSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.VerificationChannelRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.VerificationChannelService;
import org.example.mozika.specification.VerificationChannelSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultVerificationChannelService implements VerificationChannelService {
	private final VerificationChannelRepository verificationChannelRepository;

	public DefaultVerificationChannelService(VerificationChannelRepository verificationChannelRepository) {
	   this.verificationChannelRepository = verificationChannelRepository;
	}

	@Override
	public String exportVerificationChannelToCSV(List<VerificationChannel> verificationChannel) {
	   return ExportUtils.generateCsv(verificationChannel);
	}  

	@Override
	public Page<VerificationChannel> getAllVerificationChannel(Pageable pageable) {
	    try {
	        return verificationChannelRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving verification channel", ex);
	    }
	}

	@Override
	public Page<VerificationChannel> getAllVerificationChannel(Pageable pageable, VerificationChannelSearch object) {
	    try {
	        Specification<VerificationChannel> spec=VerificationChannelSpecification.filter(object);
	        return verificationChannelRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving verification channel", ex);
	    }
	}

	@Override
	public VerificationChannel getVerificationChannelById(Long id) {
	    Optional<VerificationChannel> verificationChannel = verificationChannelRepository.findById(id);
	    if (verificationChannel.isPresent()) {
	        return verificationChannel.get();
	    } else {
	        throw new ResourceNotFoundException("VerificationChannel not found with id : " + id);
	    }
	}

	@Override
	public VerificationChannel createVerificationChannel(VerificationChannel verificationChannel) {
	    try {
	        return verificationChannelRepository.save(verificationChannel);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating verification channel", ex);
	    }
	}

	@Override
	public VerificationChannel updateVerificationChannel(Long id, VerificationChannel verificationChannel) {
	    Optional<VerificationChannel> existingVerificationChannel = verificationChannelRepository.findById(id);
	    if (existingVerificationChannel.isPresent()) {
	        verificationChannel.setId(id);
	        try {
	            return verificationChannelRepository.save(verificationChannel);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating verification channel", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("VerificationChannel not found with id : " + id);
	    }
	}

	@Override
	public void deleteVerificationChannel(Long id) {
	    try {
	        verificationChannelRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting verification channel", ex);
	    }
	}



}
