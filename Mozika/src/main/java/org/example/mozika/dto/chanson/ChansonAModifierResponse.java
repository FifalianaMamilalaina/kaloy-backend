package org.example.mozika.dto.chanson;

import java.time.LocalDate;
import java.util.List;

/**
 * L'etat d'une chanson tel que le formulaire de modification doit le
 * pre-remplir.
 *
 * Un point d'entree a part, et non la liste : GET /mes-chansons renvoie des
 * entites Song, et Song NE PORTE PAS ses genres -- ils vivent dans la table
 * de liaison song_genres, sans relation declaree cote entite.
 *
 * Sans ce DTO, le formulaire se serait ouvert en affichant « aucun genre »,
 * et l'enregistrement aurait envoye une liste vide. Or remplacerGenres()
 * efface TOUS les liens avant de reinserer : chaque modification aurait donc
 * silencieusement supprime les genres de la chanson. Le defaut n'aurait
 * produit aucune erreur, seulement une perte.
 *
 * Les adresses des medias sont renvoyees TELLES QU'ELLES SONT EN BASE, et non
 * resolues en adresses de lecture comme le fait /media. Le formulaire s'en
 * sert pour deux choses : savoir qu'un media existe deja, et le renvoyer
 * inchange s'il n'y touche pas. Une adresse resolue -- lien MinIO signe ou
 * adresse de flux -- ne pourrait pas etre reecrite en base.
 */
public record ChansonAModifierResponse(
        Long id,
        String titre,
        String paroles,
        String solfa,
        String langue,

        /** Chemin brut en base, ou null. Sert a signaler qu'un media existe. */
        String urlAudio,
        String urlVideo,
        String urlKaraoke,
        String urlPlayback,

        String auteurCompositeur,
        String arrangeur,
        Integer dureeSecondes,
        LocalDate dateSortie,

        /** Null si la chanson n'appartient a aucun album. */
        Long idAlbum,

        /** Vide si la chanson n'a aucun genre, jamais null. */
        List<Long> idsGenres) {
}
