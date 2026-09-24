package org.example.kaloy.ingestion.modele;

/**
 * Metadonnees d'une chanson, reconciliees entre les tags ID3, le fichier
 * Excel et le PDF de paroles.
 *
 * Les champs nuls sont normaux : une archive peut ne fournir ni pochette,
 * ni clip, ni paroles.
 */
public record MetadonneesChanson(
        String titre,
        String album,
        String genre,
        String dateSortie,
        Integer dureeSecondes,
        String cheminAudio,
        String cheminVideo,
        String cheminPochette,
        String paroles
) {
}
