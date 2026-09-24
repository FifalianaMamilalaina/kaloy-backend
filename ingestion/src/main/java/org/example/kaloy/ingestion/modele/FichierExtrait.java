package org.example.kaloy.ingestion.modele;

/** Un fichier issu d'une archive, une fois route par type. */
public record FichierExtrait(
        String chemin,
        String nomOriginal,
        TypeFichier type
) {
}
