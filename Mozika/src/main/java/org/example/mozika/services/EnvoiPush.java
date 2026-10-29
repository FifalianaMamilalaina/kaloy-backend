package org.example.mozika.services;

import org.example.mozika.models.User;

/**
 * Envoi d'une notification push aux appareils d'un utilisateur.
 *
 * Interface et non classe concrete pour une raison pratique : le plugin Gradle
 * de Firebase fait echouer la compilation si google-services.json est absent,
 * et ce fichier ne s'obtient que depuis la console Firebase. Le reste du sprint
 * devait pouvoir avancer et se tester sans attendre ce fichier.
 *
 * Deux implementations donc : EnvoiPushInactif, qui trace et ne fait rien, et
 * plus tard une implementation Firebase. ServiceNotifications appelle l'une ou
 * l'autre sans le savoir, et rien n'est a reecrire le jour de la bascule.
 */
public interface EnvoiPush {

    /**
     * Envoie a tous les appareils enregistres du destinataire.
     *
     * Ne leve rien : un echec d'envoi ne doit pas annuler la notification deja
     * ecrite en base. L'utilisateur la verra en ouvrant l'application, ce qui
     * vaut mieux que de perdre les deux.
     *
     * @param titre   premiere ligne affichee par le systeme
     * @param corps   le texte de la notification
     * @param type    nom du type, transmis a l'application pour la redirection
     * @param idCible identifiant de l'entite visee, ou null
     */
    void envoyer(User destinataire, String titre, String corps, String type, Long idCible);

    /** Vrai quand un envoi reel est possible. Sert aux diagnostics et aux tests. */
    boolean estActif();
}
