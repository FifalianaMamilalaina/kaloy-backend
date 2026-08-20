package org.example.mozika.services.interfaces;

import org.example.mozika.models.NotificationType;
import org.example.mozika.models.dto.NotificationTypeSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface NotificationTypeService {
    Page<NotificationType> getAllNotificationType(Pageable pageable);

    Page<NotificationType> getAllNotificationType(Pageable pageable, NotificationTypeSearch object);

    NotificationType getNotificationTypeById(Long id);

    public String exportNotificationTypeToCSV(List<NotificationType> notificationType);

    

    NotificationType createNotificationType(NotificationType notificationType);

    NotificationType updateNotificationType(Long id, NotificationType notificationType);

    void deleteNotificationType(Long id);
    

}
