package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.NotificationPreference;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.User;



public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long>, JpaSpecificationExecutor<NotificationPreference> {

List<NotificationPreference> findByUseridUsers(User useridUsers);



}
