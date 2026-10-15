package org.example.mozika.services;

import org.example.mozika.dto.ChansonEcouteeResponse;
import org.example.mozika.dto.StatistiquesArtisteResponse;
import org.example.mozika.models.Artist;
import org.example.mozika.repositories.FollowRepository;
import org.example.mozika.repositories.ListeningHistoryRepository;
import org.example.mozika.repositories.SongRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Statistiques d'un artiste sur sa propre musique.
 *
 * A ne pas confondre avec l'onglet « Activite », qui montre ce que
 * l'utilisateur a ecoute et qui il suit — sa consommation. Ici il s'agit de la
 * facon dont SA musique est ecoutee par les autres. Melanger les deux donnerait
 * un ecran ou « 137 ecoutes » ne dirait plus si l'artiste a ecoute ou s'il a
 * ete ecoute.
 *
 * L'artiste est toujours deduit du jeton, jamais passe en parametre : sinon un
 * identifiant devine suffirait a consulter les chiffres d'un concurrent.
 */
@Service
public class ServiceStatistiques {

    /** Fenetre d'observation. 30 jours et non 7 : voir StatistiquesArtisteResponse. */
    private static final int JOURS_OBSERVES = 30;

    /** Taille du classement affiche. Cinq lignes tiennent sans defilement. */
    private static final int TAILLE_TOP = 5;

    private final ListeningHistoryRepository ecouteRepository;
    private final FollowRepository abonnementRepository;
    private final SongRepository chansonRepository;
    private final ServiceArtisteConnecte serviceArtisteConnecte;

    public ServiceStatistiques(ListeningHistoryRepository ecouteRepository,
                               FollowRepository abonnementRepository,
                               SongRepository chansonRepository,
                               ServiceArtisteConnecte serviceArtisteConnecte) {
        this.ecouteRepository = ecouteRepository;
        this.abonnementRepository = abonnementRepository;
        this.chansonRepository = chansonRepository;
        this.serviceArtisteConnecte = serviceArtisteConnecte;
    }

    /**
     * Statistiques du compte connecte.
     *
     * Cinq compteurs et un classement, en un seul objet. Chaque compteur est une
     * requete d'agregation distincte plutot qu'une seule requete jointe : les
     * cinq portent sur des tables differentes, et les reunir aurait produit un
     * produit cartesien gonflant les comptes.
     *
     * Un compte de role ARTIST sans ligne dans artists renvoie 403, par
     * ServiceArtisteConnecte — la meme regle que pour les invitations.
     */
    public StatistiquesArtisteResponse mesStatistiques(String emailConnecte) {
        Artist artiste = serviceArtisteConnecte.resoudre(emailConnecte);
        LocalDateTime depuis = LocalDateTime.now().minusDays(JOURS_OBSERVES);

        List<ChansonEcouteeResponse> top = chansonRepository.compterEcoutesParChanson(
                artiste, PageRequest.of(0, TAILLE_TOP));

        return new StatistiquesArtisteResponse(
                ecouteRepository.countBySongidSongs_ArtistidArtistsAndListenedAtAfter(artiste, depuis),
                ecouteRepository.countBySongidSongs_ArtistidArtists(artiste),
                ecouteRepository.compterAuditeursDistincts(artiste),
                abonnementRepository.countByArtistidArtists(artiste),
                chansonRepository.countByArtistidArtists(artiste),
                top);
    }
}
