package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.Like;
import org.example.mozika.models.dto.LikeSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.LikeRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.LikeService;
import org.example.mozika.specification.LikeSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultLikeService implements LikeService {
	private final LikeRepository likeRepository;

	public DefaultLikeService(LikeRepository likeRepository) {
	   this.likeRepository = likeRepository;
	}

	@Override
	public String exportLikeToCSV(List<Like> like) {
	   return ExportUtils.generateCsv(like);
	}  

	@Override
	public Page<Like> getAllLike(Pageable pageable) {
	    try {
	        return likeRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving like", ex);
	    }
	}

	@Override
	public Page<Like> getAllLike(Pageable pageable, LikeSearch object) {
	    try {
	        Specification<Like> spec=LikeSpecification.filter(object);
	        return likeRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving like", ex);
	    }
	}

	@Override
	public Like getLikeById(Long id) {
	    Optional<Like> like = likeRepository.findById(id);
	    if (like.isPresent()) {
	        return like.get();
	    } else {
	        throw new ResourceNotFoundException("Like not found with id : " + id);
	    }
	}

	@Override
	public Like createLike(Like like) {
	    try {
	        return likeRepository.save(like);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating like", ex);
	    }
	}

	@Override
	public Like updateLike(Long id, Like like) {
	    Optional<Like> existingLike = likeRepository.findById(id);
	    if (existingLike.isPresent()) {
	        like.setId(id);
	        try {
	            return likeRepository.save(like);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating like", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("Like not found with id : " + id);
	    }
	}

	@Override
	public void deleteLike(Long id) {
	    try {
	        likeRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting like", ex);
	    }
	}



}
