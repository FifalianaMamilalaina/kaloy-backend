package org.example.mozika.services.interfaces;

import org.example.mozika.models.Notification;
import org.example.mozika.models.dto.NotificationSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface NotificationService {
    Page<Notification> getAllNotification(Pageable pageable);

    Page<Notification> getAllNotification(Pageable pageable, NotificationSearch object);

    Notification getNotificationById(Long id);

    public String exportNotificationToCSV(List<Notification> notification);

    

    Notification createNotification(Notification notification);

    Notification updateNotification(Long id, Notification notification);

    void deleteNotification(Long id);
    

}
