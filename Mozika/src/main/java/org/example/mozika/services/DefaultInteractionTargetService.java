package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.InteractionTarget;
import org.example.mozika.models.dto.InteractionTargetSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.InteractionTargetRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.InteractionTargetService;
import org.example.mozika.specification.InteractionTargetSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultInteractionTargetService implements InteractionTargetService {
	private final InteractionTargetRepository interactionTargetRepository;

	public DefaultInteractionTargetService(InteractionTargetRepository interactionTargetRepository) {
	   this.interactionTargetRepository = interactionTargetRepository;
	}

	@Override
	public String exportInteractionTargetToCSV(List<InteractionTarget> interactionTarget) {
	   return ExportUtils.generateCsv(interactionTarget);
	}  

	@Override
	public Page<InteractionTarget> getAllInteractionTarget(Pageable pageable) {
	    try {
	        return interactionTargetRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving interaction target", ex);
	    }
	}

	@Override
	public Page<InteractionTarget> getAllInteractionTarget(Pageable pageable, InteractionTargetSearch object) {
	    try {
	        Specification<InteractionTarget> spec=InteractionTargetSpecification.filter(object);
	        return interactionTargetRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving interaction target", ex);
	    }
	}

	@Override
	public InteractionTarget getInteractionTargetById(Long id) {
	    Optional<InteractionTarget> interactionTarget = interactionTargetRepository.findById(id);
	    if (interactionTarget.isPresent()) {
	        return interactionTarget.get();
	    } else {
	        throw new ResourceNotFoundException("InteractionTarget not found with id : " + id);
	    }
	}

	@Override
	public InteractionTarget createInteractionTarget(InteractionTarget interactionTarget) {
	    try {
	        return interactionTargetRepository.save(interactionTarget);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating interaction target", ex);
	    }
	}

	@Override
	public InteractionTarget updateInteractionTarget(Long id, InteractionTarget interactionTarget) {
	    Optional<InteractionTarget> existingInteractionTarget = interactionTargetRepository.findById(id);
	    if (existingInteractionTarget.isPresent()) {
	        interactionTarget.setId(id);
	        try {
	            return interactionTargetRepository.save(interactionTarget);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating interaction target", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("InteractionTarget not found with id : " + id);
	    }
	}

	@Override
	public void deleteInteractionTarget(Long id) {
	    try {
	        interactionTargetRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting interaction target", ex);
	    }
	}



}
