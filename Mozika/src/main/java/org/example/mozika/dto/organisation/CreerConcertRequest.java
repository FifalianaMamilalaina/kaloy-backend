package org.example.mozika.dto.organisation;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Concert declare par un artiste pour lui-meme, sans evenement parent.
 *
 * Un evenement regroupe un ou plusieurs concerts, mais un concert n'appartient
 * pas forcement a un evenement : un artiste qui joue seul declare simplement sa
 * date. La colonne concerts.event_id est nullable, le schema le prevoyait deja.
 *
 * Aucun artiste n'est transmis : c'est celui du jeton. Et contrairement a un
 * creneau d'evenement, il n'y a personne a inviter — donc rien a attendre.
 */
public class CreerConcertRequest {

    /** Intitule du concert. Facultatif : une date et un lieu suffisent. */
    private String titre;

    private String description;

    private Long idLieu;

    @Valid
    private NouveauLieuRequest nouveauLieu;

    @NotNull(message = "L'heure de debut est obligatoire")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime debut;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime fin;

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getIdLieu() {
        return idLieu;
    }

    public void setIdLieu(Long idLieu) {
        this.idLieu = idLieu;
    }

    public NouveauLieuRequest getNouveauLieu() {
        return nouveauLieu;
    }

    public void setNouveauLieu(NouveauLieuRequest nouveauLieu) {
        this.nouveauLieu = nouveauLieu;
    }

    public LocalDateTime getDebut() {
        return debut;
    }

    public void setDebut(LocalDateTime debut) {
        this.debut = debut;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }
}
