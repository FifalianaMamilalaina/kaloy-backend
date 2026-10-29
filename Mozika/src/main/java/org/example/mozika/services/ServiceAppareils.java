package org.example.mozika.services;

import org.example.mozika.models.User;
import org.example.mozika.models.UserDevice;
import org.example.mozika.repositories.UserDeviceRepository;
import org.example.mozika.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Registre des appareils d'un utilisateur, pour les notifications push.
 *
 * Service a part de ServiceNotifications : celui-ci decide QUOI notifier, celui-la
 * sait OU. Les melanger aurait donne une classe qui change pour deux raisons
 * sans rapport — un nouveau type de notification, ou un nouveau systeme
 * d'envoi.
 */
@Service
public class ServiceAppareils {

    private final UserDeviceRepository appareilRepository;
    private final UserRepository userRepository;

    public ServiceAppareils(UserDeviceRepository appareilRepository,
                            UserRepository userRepository) {
        this.appareilRepository = appareilRepository;
        this.userRepository = userRepository;
    }

    /**
     * Enregistre l'appareil, ou le repointe sur le compte connecte.
     *
     * Le cas du changement de proprietaire n'est pas theorique : un artiste se
     * deconnecte, un autre se connecte sur le meme telephone, et Firebase rend
     * le MEME jeton — il est lie a l'installation, pas au compte. Sans ce
     * repointage, l'ancien compte continuerait de recevoir les notifications
     * sur un telephone qui n'est plus le sien.
     *
     * Idempotent : l'application reenregistre a chaque ouverture, ce qui tient
     * last_seen_at a jour sans creer de doublon.
     */
    @Transactional
    public void enregistrer(String email, String token, String plateforme) {
        User utilisateur = utilisateur(email);
        LocalDateTime maintenant = LocalDateTime.now();

        UserDevice appareil = appareilRepository.findByToken(token)
                .orElseGet(() -> {
                    UserDevice nouveau = new UserDevice();
                    nouveau.setToken(token);
                    nouveau.setCreatedAt(maintenant);
                    return nouveau;
                });

        appareil.setUseridUsers(utilisateur);
        appareil.setPlatform(plateforme);
        appareil.setLastSeenAt(maintenant);
        appareilRepository.save(appareil);
    }

    /**
     * Retire un appareil, a la deconnexion.
     *
     * Limite au compte qui le demande : sans ce filtre, connaitre un jeton
     * suffirait a couper les notifications de son proprietaire. Un jeton
     * inconnu ne leve rien — la deconnexion ne doit pas echouer parce que
     * l'appareil n'etait pas enregistre.
     */
    @Transactional
    public void retirer(String email, String token) {
        User utilisateur = utilisateur(email);
        appareilRepository.findByTokenAndUseridUsers(token, utilisateur)
                .ifPresent(appareilRepository::delete);
    }

    /** Appareils du compte connecte. Sert au diagnostic et aux tests. */
    public List<UserDevice> mesAppareils(String email) {
        return appareilRepository.findByUseridUsers(utilisateur(email));
    }

    private User utilisateur(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Compte introuvable"));
    }
}
