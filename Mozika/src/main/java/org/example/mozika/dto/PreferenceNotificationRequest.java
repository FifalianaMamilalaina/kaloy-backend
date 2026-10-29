package org.example.mozika.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Activation ou desactivation d'un type de notification, pour le compte connecte.
 *
 * Le compte n'est pas transmis : il est deduit du jeton. Le passer en parametre
 * aurait permis de couper les notifications de quelqu'un d'autre.
 *
 * Un seul type par requete, et non la liste entiere : l'ecran bascule un
 * interrupteur a la fois, et envoyer tout l'etat ecraserait un reglage fait
 * depuis un autre appareil entre le chargement et l'envoi.
 */
public class PreferenceNotificationRequest {

    /** Nom dans notification_types. Un nom inconnu est refuse en 404. */
    @NotBlank(message = "Le type de notification est obligatoire")
    private String type;

    @NotNull(message = "L'etat est obligatoire")
    private Boolean isEnabled;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
