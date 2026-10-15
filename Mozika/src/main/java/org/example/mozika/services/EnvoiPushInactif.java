package org.example.mozika.services;

import org.example.mozika.models.User;
import org.example.mozika.repositories.UserDeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * Implementation utilisee tant que Firebase n'est pas configure.
 *
 * Elle n'envoie rien, mais elle trace ce qu'un envoi reel aurait fait, avec le
 * nombre d'appareils concernes. C'est ce qui rend le temps A verifiable : on
 * lit dans le journal que l'envoi a bien ete DEMANDE pour les bons
 * destinataires, et qu'il ne l'a pas ete quand la preference etait coupee.
 *
 * @ConditionalOnMissingBean : le jour ou une implementation Firebase est
 * ajoutee, elle prend la place sans qu'on ait a supprimer cette classe ni a
 * toucher aux appelants. Garder le repli evite aussi qu'un poste sans fichier
 * de configuration refuse de demarrer.
 */
@Service
@ConditionalOnMissingBean(name = "envoiPushFirebase")
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
