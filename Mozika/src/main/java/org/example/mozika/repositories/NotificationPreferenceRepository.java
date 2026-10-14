package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.NotificationPreference;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.User;
import java.util.Optional;
import org.example.mozika.models.NotificationType;



public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long>, JpaSpecificationExecutor<NotificationPreference> {

List<NotificationPreference> findByUseridUsers(User useridUsers);



// Optional et non boolean : l'absence de ligne signifie « par defaut
// active », ce que ServiceNotifications traduit en orElse(true).
Optional<NotificationPreference> findByUseridUsersAndNotificationtypeidNotificationTypes(
        User useridUsers, NotificationType notificationtypeidNotificationTypes);

}
