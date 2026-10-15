package org.example.mozika.repositories;

import org.example.mozika.models.User;
import org.example.mozika.models.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long>,
        JpaSpecificationExecutor<UserDevice> {

    /** Recherche par jeton seul : c'est lui qui identifie l'appareil, pas le couple. */
    Optional<UserDevice> findByToken(String token);

    /** Destinataires d'un envoi : tous les appareils d'un utilisateur. */
    List<UserDevice> findByUseridUsers(User useridUsers);

    /** Desinscription a la deconnexion, limitee au compte qui la demande. */
    Optional<UserDevice> findByTokenAndUseridUsers(String token, User useridUsers);
}
