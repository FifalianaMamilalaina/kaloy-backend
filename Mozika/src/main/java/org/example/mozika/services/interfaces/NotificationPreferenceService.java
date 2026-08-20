package org.example.mozika.services.interfaces;

import org.example.mozika.models.NotificationPreference;
import org.example.mozika.models.dto.NotificationPreferenceSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface NotificationPreferenceService {
    Page<NotificationPreference> getAllNotificationPreference(Pageable pageable);

    Page<NotificationPreference> getAllNotificationPreference(Pageable pageable, NotificationPreferenceSearch object);

    NotificationPreference getNotificationPreferenceById(Long id);

    public String exportNotificationPreferenceToCSV(List<NotificationPreference> notificationPreference);

    

    NotificationPreference createNotificationPreference(NotificationPreference notificationPreference);

    NotificationPreference updateNotificationPreference(Long id, NotificationPreference notificationPreference);

    void deleteNotificationPreference(Long id);
    

}
