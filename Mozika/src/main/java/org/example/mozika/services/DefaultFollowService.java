package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.Follow;
import org.example.mozika.models.dto.FollowSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.FollowRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.FollowService;
import org.example.mozika.specification.FollowSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultFollowService implements FollowService {
	private final FollowRepository followRepository;

	public DefaultFollowService(FollowRepository followRepository) {
	   this.followRepository = followRepository;
	}

	@Override
	public String exportFollowToCSV(List<Follow> follow) {
	   return ExportUtils.generateCsv(follow);
	}  

	@Override
	public Page<Follow> getAllFollow(Pageable pageable) {
	    try {
	        return followRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving follow", ex);
	    }
	}

	@Override
	public Page<Follow> getAllFollow(Pageable pageable, FollowSearch object) {
	    try {
	        Specification<Follow> spec=FollowSpecification.filter(object);
	        return followRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving follow", ex);
	    }
	}

	@Override
	public Follow getFollowById(Long id) {
	    Optional<Follow> follow = followRepository.findById(id);
	    if (follow.isPresent()) {
	        return follow.get();
	    } else {
	        throw new ResourceNotFoundException("Follow not found with id : " + id);
	    }
	}

	@Override
	public Follow createFollow(Follow follow) {
	    try {
	        return followRepository.save(follow);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating follow", ex);
	    }
	}

	@Override
	public Follow updateFollow(Long id, Follow follow) {
	    Optional<Follow> existingFollow = followRepository.findById(id);
	    if (existingFollow.isPresent()) {
	        follow.setId(id);
	        try {
	            return followRepository.save(follow);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating follow", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("Follow not found with id : " + id);
	    }
	}

	@Override
	public void deleteFollow(Long id) {
	    try {
	        followRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting follow", ex);
	    }
	}



}
