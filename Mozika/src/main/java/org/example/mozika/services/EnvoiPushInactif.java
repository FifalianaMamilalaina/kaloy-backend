package org.example.mozika.services;

import org.example.mozika.models.User;
import org.example.mozika.repositories.UserDeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Implementation utilisee quand la cle Firebase est absente.
 *
 * Elle n'envoie rien, mais elle trace ce qu'un envoi reel aurait fait, avec le
 * nombre d'appareils concernes. C'est ce qui a rendu le registre d'appareils
 * verifiable avant que Firebase soit configure : on lisait dans le journal que
 * l'envoi etait bien DEMANDE pour les bons destinataires, et qu'il ne l'etait
 * pas quand la preference etait coupee.
 *
 * Elle reste utile apres : un poste sans cle — un collegue, une machine de
 * demonstration — lance le backend sans que le push fasse echouer le
 * demarrage. Classe sans annotation, c'est ConfigurationPush qui choisit.
 */
public class EnvoiPushInactif implements EnvoiPush {

    private static final Logger journal = LoggerFactory.getLogger(EnvoiPushInactif.class);

    private final UserDeviceRepository appareilRepository;

    public EnvoiPushInactif(UserDeviceRepository appareilRepository) {
        this.appareilRepository = appareilRepository;
    }

    @Override
    public void envoyer(User destinataire, String titre, String corps, String type, Long idCible) {
        int appareils = appareilRepository.findByUseridUsers(destinataire).size();
        journal.info("Push non envoye (Firebase non configure) : destinataire={}, appareils={}, "
                        + "type={}, cible={}, titre=\"{}\"",
                destinataire.getEmail(), appareils, type, idCible, titre);
    }

    @Override
    public boolean estActif() {
        return false;
    }
}
