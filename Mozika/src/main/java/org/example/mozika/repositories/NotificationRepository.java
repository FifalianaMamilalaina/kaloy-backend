package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.Notification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

List<Notification> findByUseridUsers(User useridUsers);



// Les plus recentes d'abord : c'est l'ordre d'affichage du centre de
// notifications, et le tri en base evite de ramener toute la liste.
Page<Notification> findByUseridUsersOrderByCreatedAtDesc(User useridUsers, Pageable pageable);

// Pastille de l'onglet : un compte suffit, inutile de charger les lignes.
long countByUseridUsersAndIsReadFalse(User useridUsers);

List<Notification> findByUseridUsersAndIsReadFalse(User useridUsers);

}
