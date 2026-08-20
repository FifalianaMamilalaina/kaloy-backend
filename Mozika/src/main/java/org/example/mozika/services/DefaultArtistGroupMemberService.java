package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.ArtistGroupMember;
import org.example.mozika.models.dto.ArtistGroupMemberSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.ArtistGroupMemberRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.ArtistGroupMemberService;
import org.example.mozika.specification.ArtistGroupMemberSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultArtistGroupMemberService implements ArtistGroupMemberService {
	private final ArtistGroupMemberRepository artistGroupMemberRepository;

	public DefaultArtistGroupMemberService(ArtistGroupMemberRepository artistGroupMemberRepository) {
	   this.artistGroupMemberRepository = artistGroupMemberRepository;
	}

	@Override
	public String exportArtistGroupMemberToCSV(List<ArtistGroupMember> artistGroupMember) {
	   return ExportUtils.generateCsv(artistGroupMember);
	}  

	@Override
	public Page<ArtistGroupMember> getAllArtistGroupMember(Pageable pageable) {
	    try {
	        return artistGroupMemberRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving artist group member", ex);
	    }
	}

	@Override
	public Page<ArtistGroupMember> getAllArtistGroupMember(Pageable pageable, ArtistGroupMemberSearch object) {
	    try {
	        Specification<ArtistGroupMember> spec=ArtistGroupMemberSpecification.filter(object);
	        return artistGroupMemberRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving artist group member", ex);
	    }
	}

	@Override
	public ArtistGroupMember getArtistGroupMemberById(Long id) {
	    Optional<ArtistGroupMember> artistGroupMember = artistGroupMemberRepository.findById(id);
	    if (artistGroupMember.isPresent()) {
	        return artistGroupMember.get();
	    } else {
	        throw new ResourceNotFoundException("ArtistGroupMember not found with id : " + id);
	    }
	}

	@Override
	public ArtistGroupMember createArtistGroupMember(ArtistGroupMember artistGroupMember) {
	    try {
	        return artistGroupMemberRepository.save(artistGroupMember);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating artist group member", ex);
	    }
	}

	@Override
	public ArtistGroupMember updateArtistGroupMember(Long id, ArtistGroupMember artistGroupMember) {
	    Optional<ArtistGroupMember> existingArtistGroupMember = artistGroupMemberRepository.findById(id);
	    if (existingArtistGroupMember.isPresent()) {
	        artistGroupMember.setId(id);
	        try {
	            return artistGroupMemberRepository.save(artistGroupMember);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating artist group member", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("ArtistGroupMember not found with id : " + id);
	    }
	}

	@Override
	public void deleteArtistGroupMember(Long id) {
	    try {
	        artistGroupMemberRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting artist group member", ex);
	    }
	}



}
