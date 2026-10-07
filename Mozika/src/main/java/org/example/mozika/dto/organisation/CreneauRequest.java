package org.example.mozika.dto.organisation;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Un creneau de la programmation : un artiste, un lieu, une heure.
 *
 * C'est ce qui devient une ligne de la table concerts. Si l'artiste n'est pas
 * l'organisateur, le creneau part en PENDING et constitue l'invitation qu'il
 * verra dans ses notifications.
 *
 * Le lieu se designe soit par [idLieu] pour une salle deja connue, soit par
 * [nouveauLieu] pour une salle a creer — l'un ou l'autre, jamais les deux.
 */
public class CreneauRequest {

    @NotNull(message = "L'artiste invite est obligatoire")
    private Long idArtiste;

    private Long idLieu;

    @Valid
    private NouveauLieuRequest nouveauLieu;

    @NotNull(message = "L'heure de debut est obligatoire")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime debut;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime fin;

    /** Intitule du passage (« Scene principale J1 »). Facultatif. */
    private String titre;

    public Long getIdArtiste() {
        return idArtiste;
    }

    public void setIdArtiste(Long idArtiste) {
        this.idArtiste = idArtiste;
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

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }
}
