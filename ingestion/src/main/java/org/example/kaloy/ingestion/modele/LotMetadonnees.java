package org.example.kaloy.ingestion.modele;

import java.util.List;

/**
 * Message du Programme 2 vers le Programme 3.
 *
 * Le lot reste groupe par archive : le nettoyage final (Programme 4) porte
 * sur l'archive entiere, pas sur une chanson isolee.
 */
public record LotMetadonnees(
        String nomArchive,
        String cheminArchive,
        String nomArtiste,
        String dossierTravail,
        List<MetadonneesChanson> chansons
) {
}
