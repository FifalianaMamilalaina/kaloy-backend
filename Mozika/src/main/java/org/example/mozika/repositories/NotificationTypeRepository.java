package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.NotificationType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long>, JpaSpecificationExecutor<NotificationType> {

}
