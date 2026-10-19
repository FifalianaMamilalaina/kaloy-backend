package org.example.mozika.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Ouverture d'une session d'envoi par morceaux.
 *
 * La taille totale est annoncee d'avance, et non deduite a la fin. Deux raisons :
 * un fichier trop volumineux est refuse tout de suite plutot qu'apres quarante
 * minutes d'envoi, et le serveur sait combien de morceaux attendre, donc il peut
 * dire si l'envoi est complet.
 */
public class CreerSessionEnvoiRequest {

    /** Nom d'origine, a titre indicatif. Il n'est jamais reutilise sur le disque. */
    @NotBlank(message = "Le nom du fichier est obligatoire")
    private String nomFichier;

    @NotBlank(message = "Le type du fichier est obligatoire")
    private String typeMime;

    @NotNull(message = "La taille totale est obligatoire")
    @Min(value = 1, message = "La taille totale doit etre positive")
    private Long tailleTotale;

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getTypeMime() {
        return typeMime;
    }

    public void setTypeMime(String typeMime) {
        this.typeMime = typeMime;
    }

    public Long getTailleTotale() {
        return tailleTotale;
    }

    public void setTailleTotale(Long tailleTotale) {
        this.tailleTotale = tailleTotale;
    }
}
