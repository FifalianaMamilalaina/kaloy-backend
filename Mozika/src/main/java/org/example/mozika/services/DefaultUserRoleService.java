package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.UserRole;
import org.example.mozika.models.dto.UserRoleSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.UserRoleRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.UserRoleService;
import org.example.mozika.specification.UserRoleSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultUserRoleService implements UserRoleService {
	private final UserRoleRepository userRoleRepository;

	public DefaultUserRoleService(UserRoleRepository userRoleRepository) {
	   this.userRoleRepository = userRoleRepository;
	}

	@Override
	public String exportUserRoleToCSV(List<UserRole> userRole) {
	   return ExportUtils.generateCsv(userRole);
	}  

	@Override
	public Page<UserRole> getAllUserRole(Pageable pageable) {
	    try {
	        return userRoleRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving user role", ex);
	    }
	}

	@Override
	public Page<UserRole> getAllUserRole(Pageable pageable, UserRoleSearch object) {
	    try {
	        Specification<UserRole> spec=UserRoleSpecification.filter(object);
	        return userRoleRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving user role", ex);
	    }
	}

	@Override
	public UserRole getUserRoleById(Long id) {
	    Optional<UserRole> userRole = userRoleRepository.findById(id);
	    if (userRole.isPresent()) {
	        return userRole.get();
	    } else {
	        throw new ResourceNotFoundException("UserRole not found with id : " + id);
	    }
	}

	@Override
	public UserRole createUserRole(UserRole userRole) {
	    try {
	        return userRoleRepository.save(userRole);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating user role", ex);
	    }
	}

	@Override
	public UserRole updateUserRole(Long id, UserRole userRole) {
	    Optional<UserRole> existingUserRole = userRoleRepository.findById(id);
	    if (existingUserRole.isPresent()) {
	        userRole.setId(id);
	        try {
	            return userRoleRepository.save(userRole);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating user role", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("UserRole not found with id : " + id);
	    }
	}

	@Override
	public void deleteUserRole(Long id) {
	    try {
	        userRoleRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting user role", ex);
	    }
	}



}
