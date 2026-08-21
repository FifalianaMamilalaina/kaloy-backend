package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.UserStatusHistory;
import org.example.mozika.models.dto.UserStatusHistorySearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.UserStatusHistoryRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.UserStatusHistoryService;
import org.example.mozika.specification.UserStatusHistorySpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultUserStatusHistoryService implements UserStatusHistoryService {
	private final UserStatusHistoryRepository userStatusHistoryRepository;

	public DefaultUserStatusHistoryService(UserStatusHistoryRepository userStatusHistoryRepository) {
	   this.userStatusHistoryRepository = userStatusHistoryRepository;
	}

	@Override
	public String exportUserStatusHistoryToCSV(List<UserStatusHistory> userStatusHistory) {
	   return ExportUtils.generateCsv(userStatusHistory);
	}  

	@Override
	public Page<UserStatusHistory> getAllUserStatusHistory(Pageable pageable) {
	    try {
	        return userStatusHistoryRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving user status history", ex);
	    }
	}

	@Override
	public Page<UserStatusHistory> getAllUserStatusHistory(Pageable pageable, UserStatusHistorySearch object) {
	    try {
	        Specification<UserStatusHistory> spec=UserStatusHistorySpecification.filter(object);
	        return userStatusHistoryRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving user status history", ex);
	    }
	}

	@Override
	public UserStatusHistory getUserStatusHistoryById(Long id) {
	    Optional<UserStatusHistory> userStatusHistory = userStatusHistoryRepository.findById(id);
	    if (userStatusHistory.isPresent()) {
	        return userStatusHistory.get();
	    } else {
	        throw new ResourceNotFoundException("UserStatusHistory not found with id : " + id);
	    }
	}

	@Override
	public UserStatusHistory createUserStatusHistory(UserStatusHistory userStatusHistory) {
	    try {
	        return userStatusHistoryRepository.save(userStatusHistory);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating user status history", ex);
	    }
	}

	@Override
	public UserStatusHistory updateUserStatusHistory(Long id, UserStatusHistory userStatusHistory) {
	    Optional<UserStatusHistory> existingUserStatusHistory = userStatusHistoryRepository.findById(id);
	    if (existingUserStatusHistory.isPresent()) {
	        userStatusHistory.setId(id);
	        try {
	            return userStatusHistoryRepository.save(userStatusHistory);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating user status history", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("UserStatusHistory not found with id : " + id);
	    }
	}

	@Override
	public void deleteUserStatusHistory(Long id) {
	    try {
	        userStatusHistoryRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting user status history", ex);
	    }
	}



}
