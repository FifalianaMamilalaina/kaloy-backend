package org.example.mozika.dto.organisation;

import jakarta.validation.constraints.NotBlank;

/** Lieu cree a la volee quand l'organisateur programme dans une salle absente de la base. */
public class NouveauLieuRequest {

    @NotBlank(message = "Le nom du lieu est obligatoire")
    private String nom;

    private String localisation;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }
}
