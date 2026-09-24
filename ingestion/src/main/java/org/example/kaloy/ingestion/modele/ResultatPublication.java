package org.example.kaloy.ingestion.modele;

import java.util.List;

/**
 * Message du Programme 3 vers le Programme 4.
 *
 * @param succes true seulement si TOUTES les chansons de l'archive ont ete
 *               publiees. Le Programme 4 ne supprime rien dans le cas
 *               contraire : le cahier des charges impose qu'un contenu en
 *               echec reste dans le depot, corrigible.
 * @param erreurs motifs d'echec, destines a la notification et au journal
 */
public record ResultatPublication(
        String nomArchive,
        String cheminArchive,
        String nomArtiste,
        String dossierTravail,
        boolean succes,
        int nombrePubliees,
        List<String> erreurs
) {
}
