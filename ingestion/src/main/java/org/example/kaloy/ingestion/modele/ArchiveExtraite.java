package org.example.kaloy.ingestion.modele;

import java.util.List;

/**
 * Message du Programme 1 vers le Programme 2.
 *
 * @param nomArchive    nom du fichier .zip d'origine, conserve pour la
 *                      tracabilite et pour le nettoyage final
 * @param cheminArchive chemin complet de l'archive dans le depot
 * @param nomArtiste    nom de scene deduit du nom de l'archive
 * @param dossierTravail dossier d'extraction
 * @param fichiers      contenu de l'archive, route par type
 */
public record ArchiveExtraite(
        String nomArchive,
        String cheminArchive,
        String nomArtiste,
        String dossierTravail,
        List<FichierExtrait> fichiers
) {
}
