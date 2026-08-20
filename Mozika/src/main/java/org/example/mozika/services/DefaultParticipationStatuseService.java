package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.ParticipationStatuse;
import org.example.mozika.models.dto.ParticipationStatuseSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.ParticipationStatuseRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.ParticipationStatuseService;
import org.example.mozika.specification.ParticipationStatuseSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultParticipationStatuseService implements ParticipationStatuseService {
	private final ParticipationStatuseRepository participationStatuseRepository;

	public DefaultParticipationStatuseService(ParticipationStatuseRepository participationStatuseRepository) {
	   this.participationStatuseRepository = participationStatuseRepository;
	}

	@Override
	public String exportParticipationStatuseToCSV(List<ParticipationStatuse> participationStatuse) {
	   return ExportUtils.generateCsv(participationStatuse);
	}  

	@Override
	public Page<ParticipationStatuse> getAllParticipationStatuse(Pageable pageable) {
	    try {
	        return participationStatuseRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving participation statuse", ex);
	    }
	}

	@Override
	public Page<ParticipationStatuse> getAllParticipationStatuse(Pageable pageable, ParticipationStatuseSearch object) {
	    try {
	        Specification<ParticipationStatuse> spec=ParticipationStatuseSpecification.filter(object);
	        return participationStatuseRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving participation statuse", ex);
	    }
	}

	@Override
	public ParticipationStatuse getParticipationStatuseById(Long id) {
	    Optional<ParticipationStatuse> participationStatuse = participationStatuseRepository.findById(id);
	    if (participationStatuse.isPresent()) {
	        return participationStatuse.get();
	    } else {
	        throw new ResourceNotFoundException("ParticipationStatuse not found with id : " + id);
	    }
	}

	@Override
	public ParticipationStatuse createParticipationStatuse(ParticipationStatuse participationStatuse) {
	    try {
	        return participationStatuseRepository.save(participationStatuse);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating participation statuse", ex);
	    }
	}

	@Override
	public ParticipationStatuse updateParticipationStatuse(Long id, ParticipationStatuse participationStatuse) {
	    Optional<ParticipationStatuse> existingParticipationStatuse = participationStatuseRepository.findById(id);
	    if (existingParticipationStatuse.isPresent()) {
	        participationStatuse.setId(id);
	        try {
	            return participationStatuseRepository.save(participationStatuse);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating participation statuse", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("ParticipationStatuse not found with id : " + id);
	    }
	}

	@Override
	public void deleteParticipationStatuse(Long id) {
	    try {
	        participationStatuseRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting participation statuse", ex);
	    }
	}



}
