package org.example.mozika.services;

import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.Artist;
import org.example.mozika.models.Concert;
import org.example.mozika.models.ParticipationStatuse;
import org.example.mozika.models.User;
import org.example.mozika.repositories.ArtistRepository;
import org.example.mozika.repositories.ConcertRepository;
import org.example.mozika.repositories.ParticipationStatuseRepository;
import org.example.mozika.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Invitations recues par un artiste a participer a un evenement.
 *
 * Une invitation n'est pas une entite distincte : c'est une ligne de la table
 * concerts dont le statut vaut PENDING. L'organisateur cree le creneau, et
 * l'artiste invite repond. Accepter met le statut a CONFIRMED, ce qui suffit a
 * faire apparaitre le concert dans son calendrier, lequel ne retient que les
 * confirmes. Refuser met DECLINED, et le concert n'apparait alors nulle part.
 *
 * Pourquoi un service dedie plutot que le PUT /concerts/{id} existant :
 * celui-ci remplace la ligne entiere par le corps recu, donc tout champ absent
 * de ce corps est mis a null — y compris created_by_artist_id, qui identifie
 * l'organisateur. Repondre a une invitation ne doit toucher que deux colonnes.
 */
@Service
public class ServiceInvitations {

    public static final String STATUT_EN_ATTENTE = "PENDING";
    public static final String STATUT_ACCEPTE = "CONFIRMED";
    public static final String STATUT_REFUSE = "DECLINED";

    private final ConcertRepository concertRepository;
    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final ParticipationStatuseRepository statutRepository;

    public ServiceInvitations(ConcertRepository concertRepository,
                              ArtistRepository artistRepository,
                              UserRepository userRepository,
                              ParticipationStatuseRepository statutRepository) {
        this.concertRepository = concertRepository;
        this.artistRepository = artistRepository;
        this.userRepository = userRepository;
        this.statutRepository = statutRepository;
    }

    /**
     * Invitations en attente de l'artiste connecte, de la plus proche a la plus
     * lointaine.
     *
     * Les invitations dont la date est passee sont ecartees : on ne repond plus
     * a un concert qui a deja eu lieu. La liste ne depend d'aucun parametre
     * fourni par le client — l'artiste est deduit du jeton, ce qui empeche de
     * consulter les invitations de quelqu'un d'autre.
     */
    public List<Concert> mesInvitationsEnAttente(String emailConnecte) {
        Artist artiste = artisteConnecte(emailConnecte);
        LocalDateTime maintenant = LocalDateTime.now();

        return concertRepository.findByArtistidArtists(artiste).stream()
                .filter(c -> c.getStatusidParticipationStatuses() != null
                        && STATUT_EN_ATTENTE.equals(c.getStatusidParticipationStatuses().getName()))
                .filter(c -> c.getStartTime() != null && c.getStartTime().isAfter(maintenant))
                .sorted(Comparator.comparing(Concert::getStartTime))
                .toList();
    }

    /**
     * Enregistre la reponse de l'artiste connecte a une invitation.
     *
     * Les controles sont faits ici et non cote mobile : une interface peut etre
     * contournee, et accepter un concert a la place d'un autre artiste aurait
     * des consequences reelles sur la programmation d'un evenement.
     */
    public Concert repondre(String emailConnecte, Long idConcert, String reponse) {
        if (!STATUT_ACCEPTE.equals(reponse) && !STATUT_REFUSE.equals(reponse)) {
            throw new IllegalArgumentException(
                    "Reponse invalide : " + reponse + " (CONFIRMED ou DECLINED attendu)");
        }

        Artist artiste = artisteConnecte(emailConnecte);
        Concert concert = concertRepository.findById(idConcert)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invitation introuvable avec l'id : " + idConcert));

        if (concert.getArtistidArtists() == null
                || !concert.getArtistidArtists().getId().equals(artiste.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Cette invitation ne vous est pas adressee");
        }

        String statutActuel = concert.getStatusidParticipationStatuses() == null
                ? null : concert.getStatusidParticipationStatuses().getName();
        if (!STATUT_EN_ATTENTE.equals(statutActuel)) {
            // Une reponse est definitive : la renvoyer en 409 plutot que de
            // l'ecraser silencieusement evite qu'un double appui sur le bouton
            // transforme un refus en acceptation.
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Vous avez deja repondu a cette invitation");
        }

        if (concert.getStartTime() == null || !concert.getStartTime().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La date de ce concert est passee : l'invitation ne peut plus etre acceptee ni refusee");
        }

        ParticipationStatuse statut = statutRepository.findByName(reponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Statut de participation introuvable : " + reponse));

        concert.setStatusidParticipationStatuses(statut);
        concert.setRespondedAt(LocalDateTime.now());
        return concertRepository.save(concert);
    }

    /**
     * Artiste rattache au compte connecte.
     *
     * Un compte de role ARTIST sans ligne dans artists existe en base (le compte
     * de developpement artist@dev.com) : on renvoie alors 403 plutot que de
     * laisser remonter une erreur technique.
     */
    private Artist artisteConnecte(String email) {
        User utilisateur = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Compte introuvable"));

        List<Artist> artistes = artistRepository.findByUseridUsers(utilisateur);
        if (artistes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Ce compte n'est rattache a aucun profil artiste");
        }
        return artistes.get(0);
    }
}
