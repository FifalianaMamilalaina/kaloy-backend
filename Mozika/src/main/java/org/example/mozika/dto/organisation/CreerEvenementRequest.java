package org.example.mozika.dto.organisation;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Creation d'un evenement par l'artiste organisateur.
 *
 * L'organisateur n'est pas transmis : il est deduit du jeton. Le laisser au
 * client aurait permis de creer un evenement au nom d'un autre artiste.
 *
 * Les creneaux sont facultatifs : on peut declarer l'evenement d'abord et
 * completer la programmation ensuite, ce qui correspond a la realite d'une
 * affiche qui se construit au fil des reponses.
 */
public class CreerEvenementRequest {

    @NotBlank(message = "Le nom de l'evenement est obligatoire")
    private String nom;

    private String description;

    @NotNull(message = "La date de debut est obligatoire")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFin;

    /** Liste vide par defaut : un corps sans « creneaux » ne doit pas echouer. */
    @Valid
    private List<CreneauRequest> creneaux = new ArrayList<>();

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public List<CreneauRequest> getCreneaux() {
        return creneaux == null ? new ArrayList<>() : creneaux;
    }

    public void setCreneaux(List<CreneauRequest> creneaux) {
        this.creneaux = creneaux;
    }
}
