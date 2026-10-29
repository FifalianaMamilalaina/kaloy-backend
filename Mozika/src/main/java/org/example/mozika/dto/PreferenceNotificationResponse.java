package org.example.mozika.dto;

/**
 * Etat effectif d'une preference de notification, pour un type et un compte.
 *
 * Volontairement reduit au nom technique du type et a son etat : le libelle
 * affiche reste cote mobile, qui sait seul dans quelle langue et avec quelle
 * formulation presenter « CONCERT_INVITATION ». Envoyer un libelle depuis le
 * serveur obligerait a le traduire la-bas, ou a le changer des deux cotes.
 *
 * « Effectif » et non « enregistre » : une preference absente en base vaut
 * activee, et c'est cette valeur-la qui est renvoyee.
 */
public class PreferenceNotificationResponse {

    private String type;
    private Boolean isEnabled;

    public PreferenceNotificationResponse(String type, Boolean isEnabled) {
        this.type = type;
        this.isEnabled = isEnabled;
    }

    public String getType() {
        return type;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }
}
