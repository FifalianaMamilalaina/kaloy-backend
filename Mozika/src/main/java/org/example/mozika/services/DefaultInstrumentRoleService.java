package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.InstrumentRole;
import org.example.mozika.models.dto.InstrumentRoleSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.InstrumentRoleRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.InstrumentRoleService;
import org.example.mozika.specification.InstrumentRoleSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultInstrumentRoleService implements InstrumentRoleService {
	private final InstrumentRoleRepository instrumentRoleRepository;

	public DefaultInstrumentRoleService(InstrumentRoleRepository instrumentRoleRepository) {
	   this.instrumentRoleRepository = instrumentRoleRepository;
	}

	@Override
	public String exportInstrumentRoleToCSV(List<InstrumentRole> instrumentRole) {
	   return ExportUtils.generateCsv(instrumentRole);
	}  

	@Override
	public Page<InstrumentRole> getAllInstrumentRole(Pageable pageable) {
	    try {
	        return instrumentRoleRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving instrument role", ex);
	    }
	}

	@Override
	public Page<InstrumentRole> getAllInstrumentRole(Pageable pageable, InstrumentRoleSearch object) {
	    try {
	        Specification<InstrumentRole> spec=InstrumentRoleSpecification.filter(object);
	        return instrumentRoleRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving instrument role", ex);
	    }
	}

	@Override
	public InstrumentRole getInstrumentRoleById(Long id) {
	    Optional<InstrumentRole> instrumentRole = instrumentRoleRepository.findById(id);
	    if (instrumentRole.isPresent()) {
	        return instrumentRole.get();
	    } else {
	        throw new ResourceNotFoundException("InstrumentRole not found with id : " + id);
	    }
	}

	@Override
	public InstrumentRole createInstrumentRole(InstrumentRole instrumentRole) {
	    try {
	        return instrumentRoleRepository.save(instrumentRole);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating instrument role", ex);
	    }
	}

	@Override
	public InstrumentRole updateInstrumentRole(Long id, InstrumentRole instrumentRole) {
	    Optional<InstrumentRole> existingInstrumentRole = instrumentRoleRepository.findById(id);
	    if (existingInstrumentRole.isPresent()) {
	        instrumentRole.setId(id);
	        try {
	            return instrumentRoleRepository.save(instrumentRole);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating instrument role", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("InstrumentRole not found with id : " + id);
	    }
	}

	@Override
	public void deleteInstrumentRole(Long id) {
	    try {
	        instrumentRoleRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting instrument role", ex);
	    }
	}



}
