package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.Notification;
import org.example.mozika.models.dto.NotificationSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.NotificationRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.NotificationService;
import org.example.mozika.specification.NotificationSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;


@Service
public class DefaultNotificationService implements NotificationService {
	private final NotificationRepository notificationRepository;

	public DefaultNotificationService(NotificationRepository notificationRepository) {
	   this.notificationRepository = notificationRepository;
	}

	@Override
	public String exportNotificationToCSV(List<Notification> notification) {
	   return ExportUtils.generateCsv(notification);
	}  

	@Override
	public Page<Notification> getAllNotification(Pageable pageable) {
	    try {
	        return notificationRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving notification", ex);
	    }
	}

	@Override
	public Page<Notification> getAllNotification(Pageable pageable, NotificationSearch object) {
	    try {
	        Specification<Notification> spec=NotificationSpecification.filter(object);
	        return notificationRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving notification", ex);
	    }
	}

	@Override
	public Notification getNotificationById(Long id) {
	    Optional<Notification> notification = notificationRepository.findById(id);
	    if (notification.isPresent()) {
	        return notification.get();
	    } else {
	        throw new ResourceNotFoundException("Notification not found with id : " + id);
	    }
	}

	@Override
	public Notification createNotification(Notification notification) {
	    try {
	        return notificationRepository.save(notification);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating notification", ex);
	    }
	}

	@Override
	public Notification updateNotification(Long id, Notification notification) {
	    Optional<Notification> existingNotification = notificationRepository.findById(id);
	    if (existingNotification.isPresent()) {
	        notification.setId(id);
	        try {
	            return notificationRepository.save(notification);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating notification", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("Notification not found with id : " + id);
	    }
	}

	@Override
	public void deleteNotification(Long id) {
	    try {
	        notificationRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting notification", ex);
	    }
	}



}
