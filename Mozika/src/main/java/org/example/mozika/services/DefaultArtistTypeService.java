package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.ArtistType;
import org.example.mozika.models.dto.ArtistTypeSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.ArtistTypeRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.ArtistTypeService;
import org.example.mozika.specification.ArtistTypeSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultArtistTypeService implements ArtistTypeService {
	private final ArtistTypeRepository artistTypeRepository;

	public DefaultArtistTypeService(ArtistTypeRepository artistTypeRepository) {
	   this.artistTypeRepository = artistTypeRepository;
	}

	@Override
	public String exportArtistTypeToCSV(List<ArtistType> artistType) {
	   return ExportUtils.generateCsv(artistType);
	}  

	@Override
	public Page<ArtistType> getAllArtistType(Pageable pageable) {
	    try {
	        return artistTypeRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving artist type", ex);
	    }
	}

	@Override
	public Page<ArtistType> getAllArtistType(Pageable pageable, ArtistTypeSearch object) {
	    try {
	        Specification<ArtistType> spec=ArtistTypeSpecification.filter(object);
	        return artistTypeRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving artist type", ex);
	    }
	}

	@Override
	public ArtistType getArtistTypeById(Long id) {
	    Optional<ArtistType> artistType = artistTypeRepository.findById(id);
	    if (artistType.isPresent()) {
	        return artistType.get();
	    } else {
	        throw new ResourceNotFoundException("ArtistType not found with id : " + id);
	    }
	}

	@Override
	public ArtistType createArtistType(ArtistType artistType) {
	    try {
	        return artistTypeRepository.save(artistType);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating artist type", ex);
	    }
	}

	@Override
	public ArtistType updateArtistType(Long id, ArtistType artistType) {
	    Optional<ArtistType> existingArtistType = artistTypeRepository.findById(id);
	    if (existingArtistType.isPresent()) {
	        artistType.setId(id);
	        try {
	            return artistTypeRepository.save(artistType);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating artist type", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("ArtistType not found with id : " + id);
	    }
	}

	@Override
	public void deleteArtistType(Long id) {
	    try {
	        artistTypeRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting artist type", ex);
	    }
	}



}
