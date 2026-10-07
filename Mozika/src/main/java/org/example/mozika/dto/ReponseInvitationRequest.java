package org.example.mozika.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Reponse d'un artiste a une invitation a participer a un evenement.
 *
 * Le seul champ transmis est la reponse elle-meme : la date de reponse est
 * posee par le serveur, et non par le client, pour qu'elle ne puisse pas etre
 * antidatee.
 */
public class ReponseInvitationRequest {

    /** « CONFIRMED » ou « DECLINED ». Toute autre valeur est refusee. */
    @NotBlank(message = "La reponse est obligatoire")
    private String statut;

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
