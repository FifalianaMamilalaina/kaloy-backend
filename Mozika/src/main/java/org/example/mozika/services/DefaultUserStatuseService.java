package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.UserStatuse;
import org.example.mozika.models.dto.UserStatuseSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.UserStatuseRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.UserStatuseService;
import org.example.mozika.specification.UserStatuseSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultUserStatuseService implements UserStatuseService {
	private final UserStatuseRepository userStatuseRepository;

	public DefaultUserStatuseService(UserStatuseRepository userStatuseRepository) {
	   this.userStatuseRepository = userStatuseRepository;
	}

	@Override
	public String exportUserStatuseToCSV(List<UserStatuse> userStatuse) {
	   return ExportUtils.generateCsv(userStatuse);
	}  

	@Override
	public Page<UserStatuse> getAllUserStatuse(Pageable pageable) {
	    try {
	        return userStatuseRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving user statuse", ex);
	    }
	}

	@Override
	public Page<UserStatuse> getAllUserStatuse(Pageable pageable, UserStatuseSearch object) {
	    try {
	        Specification<UserStatuse> spec=UserStatuseSpecification.filter(object);
	        return userStatuseRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving user statuse", ex);
	    }
	}

	@Override
	public UserStatuse getUserStatuseById(Long id) {
	    Optional<UserStatuse> userStatuse = userStatuseRepository.findById(id);
	    if (userStatuse.isPresent()) {
	        return userStatuse.get();
	    } else {
	        throw new ResourceNotFoundException("UserStatuse not found with id : " + id);
	    }
	}

	@Override
	public UserStatuse createUserStatuse(UserStatuse userStatuse) {
	    try {
	        return userStatuseRepository.save(userStatuse);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating user statuse", ex);
	    }
	}

	@Override
	public UserStatuse updateUserStatuse(Long id, UserStatuse userStatuse) {
	    Optional<UserStatuse> existingUserStatuse = userStatuseRepository.findById(id);
	    if (existingUserStatuse.isPresent()) {
	        userStatuse.setId(id);
	        try {
	            return userStatuseRepository.save(userStatuse);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating user statuse", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("UserStatuse not found with id : " + id);
	    }
	}

	@Override
	public void deleteUserStatuse(Long id) {
	    try {
	        userStatuseRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting user statuse", ex);
	    }
	}



}
