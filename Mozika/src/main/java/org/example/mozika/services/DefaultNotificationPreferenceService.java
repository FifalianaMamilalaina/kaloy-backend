package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.NotificationPreference;
import org.example.mozika.models.dto.NotificationPreferenceSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.NotificationPreferenceRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.NotificationPreferenceService;
import org.example.mozika.specification.NotificationPreferenceSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultNotificationPreferenceService implements NotificationPreferenceService {
	private final NotificationPreferenceRepository notificationPreferenceRepository;

	public DefaultNotificationPreferenceService(NotificationPreferenceRepository notificationPreferenceRepository) {
	   this.notificationPreferenceRepository = notificationPreferenceRepository;
	}

	@Override
	public String exportNotificationPreferenceToCSV(List<NotificationPreference> notificationPreference) {
	   return ExportUtils.generateCsv(notificationPreference);
	}  

	@Override
	public Page<NotificationPreference> getAllNotificationPreference(Pageable pageable) {
	    try {
	        return notificationPreferenceRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving notification preference", ex);
	    }
	}

	@Override
	public Page<NotificationPreference> getAllNotificationPreference(Pageable pageable, NotificationPreferenceSearch object) {
	    try {
	        Specification<NotificationPreference> spec=NotificationPreferenceSpecification.filter(object);
	        return notificationPreferenceRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving notification preference", ex);
	    }
	}

	@Override
	public NotificationPreference getNotificationPreferenceById(Long id) {
	    Optional<NotificationPreference> notificationPreference = notificationPreferenceRepository.findById(id);
	    if (notificationPreference.isPresent()) {
	        return notificationPreference.get();
	    } else {
	        throw new ResourceNotFoundException("NotificationPreference not found with id : " + id);
	    }
	}

	@Override
	public NotificationPreference createNotificationPreference(NotificationPreference notificationPreference) {
	    try {
	        return notificationPreferenceRepository.save(notificationPreference);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating notification preference", ex);
	    }
	}

	@Override
	public NotificationPreference updateNotificationPreference(Long id, NotificationPreference notificationPreference) {
	    Optional<NotificationPreference> existingNotificationPreference = notificationPreferenceRepository.findById(id);
	    if (existingNotificationPreference.isPresent()) {
	        notificationPreference.setId(id);
	        try {
	            return notificationPreferenceRepository.save(notificationPreference);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating notification preference", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("NotificationPreference not found with id : " + id);
	    }
	}

	@Override
	public void deleteNotificationPreference(Long id) {
	    try {
	        notificationPreferenceRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting notification preference", ex);
	    }
	}



}
