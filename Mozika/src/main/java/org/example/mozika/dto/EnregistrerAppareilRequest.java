package org.example.mozika.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Enregistrement d'un appareil pour les notifications push.
 *
 * Le compte n'est pas transmis : il est deduit du jeton d'authentification.
 * L'accepter en parametre aurait permis d'inscrire son telephone sur le compte
 * de quelqu'un d'autre, et donc de recevoir ses notifications.
 */
public class EnregistrerAppareilRequest {

    /** Jeton d'enregistrement fourni par Firebase, propre a l'installation. */
    @NotBlank(message = "Le jeton de l'appareil est obligatoire")
    private String token;

    /**
     * ANDROID ou IOS.
     *
     * Valide par motif plutot que par une enumeration Java : la colonne est une
     * chaine, et une enumeration imposerait une conversion de plus pour deux
     * valeurs qui ne bougeront pas.
     */
    @NotBlank(message = "La plateforme est obligatoire")
    @Pattern(regexp = "ANDROID|IOS", message = "La plateforme doit valoir ANDROID ou IOS")
    private String platform;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
