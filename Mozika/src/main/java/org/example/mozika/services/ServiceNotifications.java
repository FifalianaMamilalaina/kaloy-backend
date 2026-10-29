package org.example.mozika.services;

import org.example.mozika.dto.NotificationResponse;
import org.example.mozika.dto.PreferenceNotificationResponse;
import org.example.mozika.models.InteractionTarget;
import org.example.mozika.models.Notification;
import org.example.mozika.models.NotificationPreference;
import org.example.mozika.models.NotificationType;
import org.example.mozika.models.User;
import org.example.mozika.repositories.InteractionTargetRepository;
import org.example.mozika.repositories.NotificationPreferenceRepository;
import org.example.mozika.repositories.NotificationRepository;
import org.example.mozika.repositories.NotificationTypeRepository;
import org.example.mozika.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Chemin unique de creation et de lecture des notifications.
 *
 * Extrait en service a part parce que tout ce qui notifie doit passer par le
 * meme endroit : c'est la seule facon de garantir qu'une preference desactivee
 * soit respectee partout. Jusqu'ici les invitations ecrivaient directement
 * dans la table concerts et ne produisaient aucune notification, ce qui rendait
 * les preferences et le push sans objet.
 *
 * Les noms de type et de cible sont passes en chaine plutot qu'en enumeration :
 * ils vivent en base, dans notification_types et interaction_targets, et une
 * enumeration Java en doublon finirait par divergerdu contenu reel des tables.
 * Un nom inconnu leve une erreur a l'appel, ce qui le fait decouvrir au premier
 * test plutot que par une ligne orpheline en base.
 */
@Service
public class ServiceNotifications {

    /** Formats des libelles, identiques a ceux du script de reprise SQL. */
    private static final DateTimeFormatter JOUR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HEURE = DateTimeFormatter.ofPattern("HH:mm");

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository typeRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final InteractionTargetRepository cibleRepository;
    private final UserRepository userRepository;
    private final EnvoiPush envoiPush;

    public ServiceNotifications(NotificationRepository notificationRepository,
                                NotificationTypeRepository typeRepository,
                                NotificationPreferenceRepository preferenceRepository,
                                InteractionTargetRepository cibleRepository,
                                UserRepository userRepository,
                                EnvoiPush envoiPush) {
        this.notificationRepository = notificationRepository;
        this.typeRepository = typeRepository;
        this.preferenceRepository = preferenceRepository;
        this.cibleRepository = cibleRepository;
        this.userRepository = userRepository;
        this.envoiPush = envoiPush;
    }

    /**
     * Cree une notification pour un destinataire, si ses preferences l'autorisent.
     *
     * Ne leve rien quand la preference est desactivee : l'appelant n'a pas a
     * savoir si la notification a ete ecrite, sinon chaque point d'appel
     * devrait gerer ce cas et la regle se retrouverait dupliquee.
     *
     * @param nomCible nom dans interaction_targets (EVENT, CONCERT, MEDIA,
     *                 SONG, COMMENT), ou null pour une notification sans cible
     * @param idCible  identifiant de l'entite visee, ignore si nomCible est null
     */
    @Transactional
    public void creer(User destinataire, String nomType, String contenu,
                      String nomCible, Long idCible) {

        NotificationType type = typeRepository.findByName(nomType)
                .orElseThrow(() -> new IllegalStateException(
                        "Type de notification inconnu : " + nomType));

        if (!estActive(destinataire, type)) {
            return;
        }

        Notification notification = new Notification();
        notification.setUseridUsers(destinataire);
        notification.setTypeidNotificationTypes(type);
        notification.setContent(contenu);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        if (nomCible != null) {
            InteractionTarget cible = cibleRepository.findByName(nomCible)
                    .orElseThrow(() -> new IllegalStateException(
                            "Cible d'interaction inconnue : " + nomCible));
            notification.setRelatedentitytypeidInteractionTargets(cible);
            notification.setRelatedEntityId(idCible);
        }

        notificationRepository.save(notification);

        // Le push part APRES l'ecriture, et dans la meme methode : il herite
        // ainsi du filtre des preferences sans un controle de plus. Couper un
        // type coupe donc la notification dans l'application ET le push, ce qui
        // est le comportement attendu et n'a rien coute a obtenir.
        //
        // Le titre est le libelle du type et non le contenu : le systeme
        // affiche la premiere ligne en gras, et « Invitation » suivi du detail
        // se lit mieux que le detail repete deux fois.
        envoiPush.envoyer(destinataire, titrePush(type.getName()), contenu,
                type.getName(), idCible);
    }

    /**
     * Premiere ligne de la notification systeme.
     *
     * Volontairement court : Android et iOS tronquent au-dela d'une quarantaine
     * de caracteres sur un ecran de telephone.
     */
    private String titrePush(String nomType) {
        return switch (nomType) {
            case "CONCERT_INVITATION", "EVENT_INVITATION" -> "Invitation";
            case "INVITATION_RESPONSE" -> "Reponse a votre invitation";
            case "FOLLOW" -> "Nouvel abonne";
            case "NEW_RELEASE" -> "Nouveaute";
            case "EVENT_REMINDER" -> "Rappel d'evenement";
            case "COMMENT_REPLY" -> "Reponse a votre commentaire";
            case "VERIFICATION" -> "Verification de compte";
            default -> "Kaloy";
        };
    }

    /**
     * Libelle d'une invitation a jouer.
     *
     * Le titre d'un concert est facultatif en base, et le concert peut n'avoir
     * aucun evenement parent. Sans ces reculs successifs, le libelle vaudrait
     * « Invitation a jouer : null » — ou leverait une NullPointerException sur
     * la concatenation. La meme logique figure dans
     * reprise_notifications_invitations.sql, qui a rencontre le cas reel du
     * concert 756.
     *
     * Methode statique et parametres primitifs : ServiceNotifications n'a pas a
     * connaitre Concert ni Event, sinon la brique commune dependrait du domaine
     * evenementiel.
     */
    public static String libelleInvitation(String titreConcert, String nomEvenement,
                                           LocalDateTime debut) {
        String titre = titreConcert == null || titreConcert.isBlank() ? null : titreConcert.trim();

        String quoi;
        if (titre != null && nomEvenement != null) {
            quoi = "Invitation a jouer : " + titre + " (" + nomEvenement + ")";
        } else if (titre != null) {
            quoi = "Invitation a jouer : " + titre;
        } else if (nomEvenement != null) {
            quoi = "Invitation a jouer a " + nomEvenement;
        } else {
            quoi = "Invitation a jouer";
        }

        if (debut == null) {
            return quoi;
        }
        return quoi + " - le " + debut.format(JOUR) + " a " + debut.format(HEURE);
    }

    /**
     * Libelle de la reponse d'un artiste, destine a l'organisateur.
     *
     * L'organisateur a parfois plusieurs invitations en cours sur le meme
     * evenement : le nom de l'artiste est donc indispensable, sinon la
     * notification ne dit pas qui a repondu.
     */
    public static String libelleReponse(String nomArtiste, boolean accepte, String titreConcert) {
        String verbe = accepte ? "a accepte" : "a refuse";
        String titre = titreConcert == null || titreConcert.isBlank() ? null : titreConcert.trim();
        return titre == null
                ? nomArtiste + " " + verbe + " votre invitation"
                : nomArtiste + " " + verbe + " votre invitation : " + titre;
    }

    /**
     * Une preference absente vaut activee.
     *
     * L'alternative — exiger une ligne par utilisateur et par type — imposerait
     * une migration creant huit lignes pour chaque compte existant, et tout
     * nouveau type arriverait desactive pour tout le monde. On n'ecrit donc une
     * ligne que lorsque l'utilisateur desactive explicitement quelque chose.
     */
    private boolean estActive(User destinataire, NotificationType type) {
        return preferenceRepository
                .findByUseridUsersAndNotificationtypeidNotificationTypes(destinataire, type)
                .map(NotificationPreference::getIsEnabled)
                .orElse(true);
    }

    /** Notifications du compte connecte, les plus recentes d'abord. */
    public Page<NotificationResponse> mesNotifications(String email, Pageable pageable) {
        User utilisateur = utilisateur(email);
        return notificationRepository
                .findByUseridUsersOrderByCreatedAtDesc(utilisateur, pageable)
                .map(this::versReponse);
    }

    /** Nombre de notifications non lues, pour la pastille de l'onglet. */
    public long mesNonLues(String email) {
        return notificationRepository.countByUseridUsersAndIsReadFalse(utilisateur(email));
    }

    /**
     * Marque une notification lue.
     *
     * L'appartenance est verifiee : sans ce controle, un identifiant devine
     * suffirait a marquer lues les notifications d'un autre compte. On renvoie
     * 404 et non 403, pour ne pas confirmer l'existence de l'identifiant.
     */
    @Transactional
    public void marquerLue(String email, Long idNotification) {
        User utilisateur = utilisateur(email);
        Notification notification = notificationRepository.findById(idNotification)
                .filter(n -> n.getUseridUsers().getId().equals(utilisateur.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Notification introuvable"));

        if (Boolean.TRUE.equals(notification.getIsRead())) {
            return;
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    /** Marque lues toutes les notifications du compte connecte. */
    @Transactional
    public void marquerToutesLues(String email) {
        List<Notification> nonLues =
                notificationRepository.findByUseridUsersAndIsReadFalse(utilisateur(email));
        nonLues.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(nonLues);
    }

    // ── Preferences ──────────────────────────────────────────────────────────

    /**
     * Etat effectif de chaque type de notification, pour le compte connecte.
     *
     * Tous les types sont renvoyes, meme ceux qu'aucun code ne produit encore :
     * le serveur ne sait pas lesquels ont un producteur, et l'inventer ici
     * demanderait une colonne a tenir a jour a la main. C'est l'application qui
     * choisit lesquels afficher.
     *
     * La fusion est faite ici et non cote mobile : la regle « preference
     * absente vaut activee » existe deja dans ce service, et la redire ailleurs
     * la ferait diverger le jour ou elle changerait.
     */
    public List<PreferenceNotificationResponse> mesPreferences(String email) {
        User utilisateur = utilisateur(email);

        Map<Long, Boolean> enregistrees = preferenceRepository.findByUseridUsers(utilisateur).stream()
                .collect(Collectors.toMap(
                        p -> p.getNotificationtypeidNotificationTypes().getId(),
                        NotificationPreference::getIsEnabled,
                        // Deux lignes pour le meme type ne devraient pas exister,
                        // aucune contrainte d'unicite ne l'empeche en base. On
                        // garde la plus restrictive plutot que de lever une
                        // erreur sur un ecran de reglages.
                        (a, b) -> Boolean.FALSE.equals(a) ? a : b));

        return typeRepository.findAll().stream()
                .map(type -> new PreferenceNotificationResponse(
                        type.getName(),
                        enregistrees.getOrDefault(type.getId(), Boolean.TRUE)))
                .toList();
    }

    /**
     * Active ou desactive un type pour le compte connecte.
     *
     * Une ligne est ecrite meme pour une activation, alors que l'absence de
     * ligne vaut deja activee : distinguer « jamais touche » de « remis a
     * activer » n'apporte rien, et l'ecriture explicite evite un cas de plus
     * a traiter ici.
     */
    @Transactional
    public void definirPreference(String email, String nomType, boolean actif) {
        User utilisateur = utilisateur(email);
        NotificationType type = typeRepository.findByName(nomType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Type de notification inconnu : " + nomType));

        NotificationPreference preference = preferenceRepository
                .findByUseridUsersAndNotificationtypeidNotificationTypes(utilisateur, type)
                .orElseGet(() -> {
                    NotificationPreference nouvelle = new NotificationPreference();
                    nouvelle.setUseridUsers(utilisateur);
                    nouvelle.setNotificationtypeidNotificationTypes(type);
                    return nouvelle;
                });

        preference.setIsEnabled(actif);
        preferenceRepository.save(preference);
    }

    private User utilisateur(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Compte introuvable"));
    }

    private NotificationResponse versReponse(Notification notification) {
        InteractionTarget cible = notification.getRelatedentitytypeidInteractionTargets();
        return new NotificationResponse(
                notification.getId(),
                notification.getTypeidNotificationTypes().getName(),
                notification.getContent(),
                cible == null ? null : cible.getName(),
                notification.getRelatedEntityId(),
                notification.getIsRead(),
                notification.getCreatedAt());
    }
}
