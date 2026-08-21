package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.NotificationType;
import org.example.mozika.models.dto.NotificationTypeSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.NotificationTypeRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.NotificationTypeService;
import org.example.mozika.specification.NotificationTypeSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultNotificationTypeService implements NotificationTypeService {
	private final NotificationTypeRepository notificationTypeRepository;

	public DefaultNotificationTypeService(NotificationTypeRepository notificationTypeRepository) {
	   this.notificationTypeRepository = notificationTypeRepository;
	}

	@Override
	public String exportNotificationTypeToCSV(List<NotificationType> notificationType) {
	   return ExportUtils.generateCsv(notificationType);
	}  

	@Override
	public Page<NotificationType> getAllNotificationType(Pageable pageable) {
	    try {
	        return notificationTypeRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving notification type", ex);
	    }
	}

	@Override
	public Page<NotificationType> getAllNotificationType(Pageable pageable, NotificationTypeSearch object) {
	    try {
	        Specification<NotificationType> spec=NotificationTypeSpecification.filter(object);
	        return notificationTypeRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving notification type", ex);
	    }
	}

	@Override
	public NotificationType getNotificationTypeById(Long id) {
	    Optional<NotificationType> notificationType = notificationTypeRepository.findById(id);
	    if (notificationType.isPresent()) {
	        return notificationType.get();
	    } else {
	        throw new ResourceNotFoundException("NotificationType not found with id : " + id);
	    }
	}

	@Override
	public NotificationType createNotificationType(NotificationType notificationType) {
	    try {
	        return notificationTypeRepository.save(notificationType);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating notification type", ex);
	    }
	}

	@Override
	public NotificationType updateNotificationType(Long id, NotificationType notificationType) {
	    Optional<NotificationType> existingNotificationType = notificationTypeRepository.findById(id);
	    if (existingNotificationType.isPresent()) {
	        notificationType.setId(id);
	        try {
	            return notificationTypeRepository.save(notificationType);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating notification type", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("NotificationType not found with id : " + id);
	    }
	}

	@Override
	public void deleteNotificationType(Long id) {
	    try {
	        notificationTypeRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting notification type", ex);
	    }
	}



}
