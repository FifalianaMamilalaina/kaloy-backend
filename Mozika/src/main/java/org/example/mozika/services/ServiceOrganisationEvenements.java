package org.example.mozika.services;

import org.example.mozika.dto.organisation.CreerEvenementRequest;
import org.example.mozika.dto.organisation.CreneauRequest;
import org.example.mozika.dto.organisation.NouveauLieuRequest;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.*;
import org.example.mozika.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Organisation d'evenements, cote artiste organisateur.
 *
 * Un evenement est une affiche : il porte un nom, des dates, et se compose de
 * creneaux. Chaque creneau est une ligne de la table concerts. Celui que
 * l'organisateur se reserve part directement en CONFIRMED — il ne va pas
 * s'inviter lui-meme — tandis que chaque autre part en PENDING et devient
 * l'invitation que l'artiste concerne verra dans ses notifications.
 *
 * Pourquoi ne pas utiliser POST /events, qui existe deja :
 *   * il laisse le client designer l'organisateur, donc creer un evenement au
 *     nom de quelqu'un d'autre ;
 *   * il demande au client de poser lui-meme moderation_status_id, created_at
 *     et le statut de participation de chaque concert, qui sont des regles
 *     serveur ;
 *   * createFullEvent itere les listes de concerts et de medias sans controle
 *     de nullite : omettre une liste produit un 500 incomprehensible.
 */
@Service
public class ServiceOrganisationEvenements {

    private static final String MODERATION_EN_ATTENTE = "PENDING";

    private final EventRepository evenementRepository;
    private final ConcertRepository concertRepository;
    private final VenueRepository lieuRepository;
    private final ArtistRepository artisteRepository;
    private final ParticipationStatuseRepository statutParticipationRepository;
    private final EventModerationStatuseRepository statutModerationRepository;
    private final ServiceArtisteConnecte serviceArtisteConnecte;

    public ServiceOrganisationEvenements(EventRepository evenementRepository,
                                         ConcertRepository concertRepository,
                                         VenueRepository lieuRepository,
                                         ArtistRepository artisteRepository,
                                         ParticipationStatuseRepository statutParticipationRepository,
                                         EventModerationStatuseRepository statutModerationRepository,
                                         ServiceArtisteConnecte serviceArtisteConnecte) {
        this.evenementRepository = evenementRepository;
        this.concertRepository = concertRepository;
        this.lieuRepository = lieuRepository;
        this.artisteRepository = artisteRepository;
        this.statutParticipationRepository = statutParticipationRepository;
        this.statutModerationRepository = statutModerationRepository;
        this.serviceArtisteConnecte = serviceArtisteConnecte;
    }

    /** Evenements organises par l'artiste connecte, du plus proche au plus lointain. */
    public List<Event> mesEvenements(String emailConnecte) {
        Artist organisateur = serviceArtisteConnecte.resoudre(emailConnecte);
        return evenementRepository.findByCreatedbyartistidArtists(organisateur).stream()
                .sorted(Comparator.comparing(Event::getStartDate).reversed())
                .toList();
    }

    /** Programmation d'un evenement : tous ses creneaux, quel que soit leur statut. */
    public List<Concert> programmation(String emailConnecte, Long idEvenement) {
        Event evenement = monEvenement(emailConnecte, idEvenement);
        return concertRepository.findByEventidEvents(evenement).stream()
                .sorted(Comparator.comparing(Concert::getStartTime))
                .toList();
    }

    /**
     * Cree l'evenement et sa premiere serie de creneaux.
     *
     * Transactionnel : un creneau invalide annule la creation entiere. Un
     * evenement a moitie programme serait pire qu'un echec franc, parce que
     * l'organisateur croirait ses invitations parties.
     */
    @Transactional
    public Event creer(String emailConnecte, CreerEvenementRequest requete) {
        Artist organisateur = serviceArtisteConnecte.resoudre(emailConnecte);

        if (requete.getDateFin().isBefore(requete.getDateDebut())) {
            throw new IllegalArgumentException(
                    "La date de fin precede la date de debut");
        }
        if (requete.getDateFin().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Cet evenement est deja termine : personne ne pourrait repondre a ses invitations");
        }

        Event evenement = new Event();
        evenement.setName(requete.getNom().trim());
        evenement.setDescription(requete.getDescription());
        evenement.setStartDate(requete.getDateDebut());
        evenement.setEndDate(requete.getDateFin());
        evenement.setCreatedbyartistidArtists(organisateur);
        // L'evenement n'a ete relu par personne : le statut dit la verite, et
        // reviewed_at reste vide. Aucun ecran ne filtre encore la-dessus, donc
        // l'evenement est visible immediatement ; le jour ou la moderation
        // existera, ces lignes seront deja dans le bon etat.
        evenement.setModerationstatusidEventModerationStatuses(statutModeration());
        evenement.setCreatedAt(LocalDateTime.now());

        Event enregistre = evenementRepository.save(evenement);

        for (CreneauRequest creneau : requete.getCreneaux()) {
            concertRepository.save(construireCreneau(enregistre, organisateur, creneau));
        }
        return enregistre;
    }

    /**
     * Ajoute des creneaux a un evenement deja cree.
     *
     * Une affiche se complete au fil des reponses : un refus doit pouvoir etre
     * remplace sans recreer l'evenement.
     */
    @Transactional
    public List<Concert> ajouterCreneaux(String emailConnecte, Long idEvenement,
                                         List<CreneauRequest> creneaux) {
        Event evenement = monEvenement(emailConnecte, idEvenement);
        Artist organisateur = evenement.getCreatedbyartistidArtists();

        return creneaux.stream()
                .map(creneau -> concertRepository.save(
                        construireCreneau(evenement, organisateur, creneau)))
                .toList();
    }

    // ── Construction d'un creneau ────────────────────────────────────────────

    private Concert construireCreneau(Event evenement, Artist organisateur, CreneauRequest requete) {
        Artist artiste = artisteRepository.findById(requete.getIdArtiste())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artiste introuvable avec l'id : " + requete.getIdArtiste()));

        LocalDateTime debut = requete.getDebut();
        if (!debut.isAfter(LocalDateTime.now())) {
            // Sans cette regle, on pourrait emettre une invitation que le
            // service des invitations refuserait ensuite de traiter, sa date
            // etant passee : elle resterait bloquee en attente a vie.
            throw new IllegalArgumentException(
                    "Le creneau du " + debut.toLocalDate() + " est deja passe");
        }
        if (debut.toLocalDate().isBefore(evenement.getStartDate())
                || debut.toLocalDate().isAfter(evenement.getEndDate())) {
            throw new IllegalArgumentException(
                    "Le creneau du " + debut.toLocalDate() + " tombe en dehors des dates de l'evenement ("
                            + evenement.getStartDate() + " au " + evenement.getEndDate() + ")");
        }
        if (requete.getFin() != null && !requete.getFin().isAfter(debut)) {
            throw new IllegalArgumentException(
                    "L'heure de fin doit suivre l'heure de debut");
        }

        boolean estLOrganisateur = artiste.getId().equals(organisateur.getId());

        Concert concert = new Concert();
        concert.setEventidEvents(evenement);
        concert.setArtistidArtists(artiste);
        concert.setVenueidVenues(resoudreLieu(requete));
        concert.setStartTime(debut);
        concert.setEndTime(requete.getFin());
        concert.setTitle(requete.getTitre());
        concert.setCreatedbyartistidArtists(organisateur);
        concert.setStatusidParticipationStatuses(statutParticipation(
                estLOrganisateur ? ServiceInvitations.STATUT_ACCEPTE : ServiceInvitations.STATUT_EN_ATTENTE));
        // L'organisateur a decide pour lui-meme : sa reponse est horodatee tout
        // de suite. Pour les autres, responded_at reste vide jusqu'a la reponse.
        concert.setRespondedAt(estLOrganisateur ? LocalDateTime.now() : null);
        concert.setModerationstatusidEventModerationStatuses(statutModeration());
        concert.setCreatedAt(LocalDateTime.now());
        return concert;
    }

    /**
     * Lieu du creneau : une salle connue, ou une nouvelle creee au passage.
     *
     * Exiger l'un ou l'autre, et pas les deux, evite d'avoir a arbitrer entre
     * deux intentions contradictoires dans la meme requete.
     */
    private Venue resoudreLieu(CreneauRequest requete) {
        boolean lieuExistant = requete.getIdLieu() != null;
        boolean lieuACreer = requete.getNouveauLieu() != null
                && requete.getNouveauLieu().getNom() != null
                && !requete.getNouveauLieu().getNom().isBlank();

        if (lieuExistant && lieuACreer) {
            throw new IllegalArgumentException(
                    "Choisissez un lieu existant ou creez-en un, pas les deux");
        }
        if (!lieuExistant && !lieuACreer) {
            throw new IllegalArgumentException("Le lieu du creneau est obligatoire");
        }

        if (lieuExistant) {
            return lieuRepository.findById(requete.getIdLieu())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Lieu introuvable avec l'id : " + requete.getIdLieu()));
        }

        NouveauLieuRequest nouveau = requete.getNouveauLieu();
        Venue lieu = new Venue();
        lieu.setName(nouveau.getNom().trim());
        lieu.setLocation(nouveau.getLocalisation() == null ? null : nouveau.getLocalisation().trim());
        return lieuRepository.save(lieu);
    }

    // ── Acces ────────────────────────────────────────────────────────────────

    /** Evenement de l'organisateur connecte, ou 403 s'il appartient a un autre. */
    private Event monEvenement(String emailConnecte, Long idEvenement) {
        Artist organisateur = serviceArtisteConnecte.resoudre(emailConnecte);
        Event evenement = evenementRepository.findById(idEvenement)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evenement introuvable avec l'id : " + idEvenement));

        Artist proprietaire = evenement.getCreatedbyartistidArtists();
        if (proprietaire == null || !proprietaire.getId().equals(organisateur.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Vous n'organisez pas cet evenement");
        }
        return evenement;
    }

    private ParticipationStatuse statutParticipation(String nom) {
        return statutParticipationRepository.findByName(nom)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Statut de participation introuvable : " + nom));
    }

    private EventModerationStatuse statutModeration() {
        return statutModerationRepository.findByName(MODERATION_EN_ATTENTE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Statut de moderation introuvable : " + MODERATION_EN_ATTENTE));
    }
}
