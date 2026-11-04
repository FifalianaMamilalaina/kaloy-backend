package org.example.mozika.dto.paroles;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Recherche de paroles par titre, avec le nom d'artiste pour departager.
 *
 * Le nom d'artiste ne sert PAS a construire l'adresse de la page. On a
 * constate que les deux catalogues ne nomment pas les memes choses de la meme
 * facon : une chanson enregistree chez nous sous un artiste peut figurer sur
 * le site sous un autre nom. Le nom sert uniquement a classer les resultats.
 */
public class RechercheParolesRequest {

    @NotBlank(message = "Le titre est obligatoire pour chercher des paroles")
    @Size(max = 150)
    private String titre;

    /** Facultatif : ameliore le classement, n'est jamais une condition. */
    @Size(max = 150)
    private String artiste;

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getArtiste() { return artiste; }
    public void setArtiste(String artiste) { this.artiste = artiste; }
}
