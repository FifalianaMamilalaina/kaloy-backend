package org.example.mozika.services;

import org.example.mozika.dto.CreerSessionEnvoiRequest;
import org.example.mozika.dto.EtatSessionEnvoiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Envoi d'un fichier par morceaux, avec reprise.
 *
 * Pourquoi ce mecanisme existe : une video de cinq minutes pese 300 a 500 Mo.
 * L'envoi en une seule requete echouait de deux facons — l'application chargeait
 * tout le fichier en memoire et tombait en OutOfMemoryError, et une coupure
 * reseau a 90 % imposait de tout reprendre.
 *
 * Decoupe en morceaux de 2 Mo, chacun envoye separement. Effet de bord heureux :
 * la limite multipart de 200 Mo n'est plus jamais approchee, il n'y a donc rien
 * a relever cote Spring.
 *
 * L'etat vit sur le disque et non en memoire :
 *
 *   uploads/.sessions/{idSession}/
 *       meta.properties     nom, type, taille totale, proprietaire, date
 *       000000              morceau 0
 *       000001              morceau 1
 *
 * La liste des morceaux recus EST la liste des fichiers presents. Rien a tenir
 * a jour, et la reprise fonctionne apres un redemarrage du serveur — ce qu'une
 * carte en memoire n'aurait pas permis. Un morceau renvoye deux fois ecrase
 * simplement le precedent, donc le reessai est sans risque.
 */
@Service
public class ServiceEnvoiParMorceaux {

    private static final Logger journal = LoggerFactory.getLogger(ServiceEnvoiParMorceaux.class);

    /** Nom du sous-dossier des sessions. Le point le masque des listages. */
    private static final String DOSSIER_SESSIONS = ".sessions";

    private static final String FICHIER_META = "meta.properties";

    /**
     * Un identifiant de session arrive par l'URL et sert a construire un chemin.
     * Sans cette validation, « ../../ » permettrait d'ecrire n'importe ou sur le
     * disque du serveur.
     */
    private static final Pattern IDENTIFIANT_VALIDE = Pattern.compile("[0-9a-f]{32}");

    @Value("${kaloy.uploads.dossier}")
    private String dossier;

    @Value("${kaloy.uploads.url-publique}")
    private String urlPublique;

    @Value("${kaloy.uploads.morceaux.taille}")
    private int tailleMorceau;

    @Value("${kaloy.uploads.morceaux.taille-max-totale}")
    private long tailleMaxTotale;

    @Value("${kaloy.uploads.morceaux.duree-vie-heures}")
    private int dureeVieHeures;

    // ── Ouverture ────────────────────────────────────────────────────────────

    /**
     * Ouvre une session et renvoie son identifiant.
     *
     * La taille et le format sont verifies ici, avant le premier octet : un
     * refus apres quarante minutes d'envoi serait inacceptable.
     */
    public EtatSessionEnvoiResponse creer(String email, CreerSessionEnvoiRequest requete) {
        String extension = FormatsAutorises.extensionPour(requete.getTypeMime());
        if (extension == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Format non supporte : " + requete.getTypeMime()
                            + " (" + FormatsAutorises.FORMATS_LISIBLES + " attendus)");
        }
        if (requete.getTailleTotale() > tailleMaxTotale) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Fichier trop volumineux : " + enMegaoctets(requete.getTailleTotale())
                            + " Mo, maximum " + enMegaoctets(tailleMaxTotale) + " Mo");
        }

        String idSession = UUID.randomUUID().toString().replace("-", "");
        Path session = dossierSession(idSession);

        try {
            Files.createDirectories(session);

            Properties meta = new Properties();
            meta.setProperty("nomFichier", requete.getNomFichier());
            meta.setProperty("typeMime", requete.getTypeMime());
            meta.setProperty("tailleTotale", String.valueOf(requete.getTailleTotale()));
            // Le proprietaire est verifie a chaque morceau : sans cela, connaitre
            // un identifiant de session suffirait a injecter des octets dans
            // l'envoi de quelqu'un d'autre.
            meta.setProperty("proprietaire", email);
            meta.setProperty("creeLe", Instant.now().toString());

            try (var sortie = Files.newOutputStream(session.resolve(FICHIER_META))) {
                meta.store(sortie, "Session d'envoi par morceaux");
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Impossible d'ouvrir la session d'envoi : " + e.getMessage());
        }

        journal.info("Session d'envoi ouverte : id={}, taille={} Mo, type={}, proprietaire={}",
                idSession, enMegaoctets(requete.getTailleTotale()), requete.getTypeMime(), email);

        return etat(email, idSession);
    }

    // ── Un morceau ───────────────────────────────────────────────────────────

    /**
     * Enregistre un morceau. Idempotent : le reenvoyer ecrase le precedent.
     *
     * L'indice est borne par le nombre de morceaux attendu. Sans ce controle, un
     * indice arbitraire remplirait le disque de fichiers que l'assemblage
     * ignorerait.
     */
    public void enregistrerMorceau(String email, String idSession, int indice, byte[] contenu) {
        Properties meta = metaVerifiee(email, idSession);
        long tailleTotale = Long.parseLong(meta.getProperty("tailleTotale"));
        int attendus = morceauxAttendus(tailleTotale);

        if (indice < 0 || indice >= attendus) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Indice de morceau hors limites : " + indice + " (0 a " + (attendus - 1) + ")");
        }
        if (contenu == null || contenu.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Morceau vide");
        }
        if (contenu.length > tailleMorceau) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Morceau trop gros : " + contenu.length + " octets, maximum " + tailleMorceau);
        }

        try {
            Files.write(dossierSession(idSession).resolve(nomMorceau(indice)), contenu,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Echec de l'ecriture du morceau " + indice + " : " + e.getMessage());
        }
    }

    // ── Etat, pour la reprise ────────────────────────────────────────────────

    public EtatSessionEnvoiResponse etat(String email, String idSession) {
        Properties meta = metaVerifiee(email, idSession);
        long tailleTotale = Long.parseLong(meta.getProperty("tailleTotale"));
        int attendus = morceauxAttendus(tailleTotale);
        List<Integer> recus = morceauxPresents(idSession);

        return new EtatSessionEnvoiResponse(idSession, tailleTotale, tailleMorceau,
                attendus, recus, recus.size() == attendus);
    }

    // ── Assemblage ───────────────────────────────────────────────────────────

    /**
     * Assemble les morceaux et renvoie l'adresse publique du fichier.
     *
     * Les morceaux sont concatenes dans l'ordre, en flux : jamais le fichier
     * entier en memoire, cote serveur non plus. La session est supprimee ensuite,
     * donc l'operation n'est pas rejouable — c'est voulu, un second appel
     * renverrait 404 plutot que de creer un doublon silencieux.
     */
    public String finir(String email, String idSession) {
        Properties meta = metaVerifiee(email, idSession);
        long tailleTotale = Long.parseLong(meta.getProperty("tailleTotale"));
        String typeMime = meta.getProperty("typeMime");
        int attendus = morceauxAttendus(tailleTotale);
        List<Integer> recus = morceauxPresents(idSession);

        if (recus.size() != attendus) {
            List<Integer> manquants = new ArrayList<>();
            for (int i = 0; i < attendus && manquants.size() < 10; i++) {
                if (!recus.contains(i)) {
                    manquants.add(i);
                }
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Envoi incomplet : " + recus.size() + " morceaux sur " + attendus
                            + ", manquent " + manquants);
        }

        String extension = FormatsAutorises.extensionPour(typeMime);
        String nom = UUID.randomUUID().toString().replace("-", "") + extension;

        Path racine = Paths.get(dossier).toAbsolutePath().normalize();
        Path destination = racine.resolve(nom);
        Path session = dossierSession(idSession);

        try {
            Files.createDirectories(racine);
            long ecrits = 0;
            try (OutputStream sortie = Files.newOutputStream(destination,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (int i = 0; i < attendus; i++) {
                    ecrits += Files.copy(session.resolve(nomMorceau(i)), sortie);
                }
            }

            // Un ecart signale un morceau tronque en cours d'envoi. Mieux vaut
            // le decouvrir ici que de livrer une video illisible.
            if (ecrits != tailleTotale) {
                Files.deleteIfExists(destination);
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Taille assemblee incoherente : " + ecrits + " octets contre "
                                + tailleTotale + " annonces. Reprenez l'envoi.");
            }

            supprimerDossier(session);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Echec de l'assemblage : " + e.getMessage());
        }

        journal.info("Envoi termine : id={}, {} Mo assembles vers {}",
                idSession, enMegaoctets(tailleTotale), nom);

        return urlPublique.endsWith("/") ? urlPublique + nom : urlPublique + "/" + nom;
    }

    // ── Nettoyage ────────────────────────────────────────────────────────────

    /**
     * Supprime les sessions abandonnees.
     *
     * Sans cela chaque envoi interrompu laisse ses morceaux sur le disque pour
     * toujours, et il s'agit de centaines de megaoctets. Une fois par heure
     * suffit : la duree de vie est de plusieurs heures.
     */
    @Scheduled(fixedDelay = 3_600_000L)
    public void purgerSessionsAbandonnees() {
        Path racineSessions = Paths.get(dossier).toAbsolutePath().normalize()
                .resolve(DOSSIER_SESSIONS);
        if (!Files.isDirectory(racineSessions)) {
            return;
        }

        Instant limite = Instant.now().minus(Duration.ofHours(dureeVieHeures));
        try (Stream<Path> sessions = Files.list(racineSessions)) {
            sessions.filter(Files::isDirectory).forEach(session -> {
                try {
                    Instant creee = Files.getLastModifiedTime(session).toInstant();
                    if (creee.isBefore(limite)) {
                        supprimerDossier(session);
                        journal.info("Session d'envoi abandonnee supprimee : {}",
                                session.getFileName());
                    }
                } catch (IOException e) {
                    journal.warn("Purge impossible pour {} : {}", session.getFileName(),
                            e.getMessage());
                }
            });
        } catch (IOException e) {
            journal.warn("Purge des sessions impossible : {}", e.getMessage());
        }
    }

    // ── Outils ───────────────────────────────────────────────────────────────

    private Path dossierSession(String idSession) {
        if (!IDENTIFIANT_VALIDE.matcher(idSession).matches()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session introuvable");
        }
        return Paths.get(dossier).toAbsolutePath().normalize()
                .resolve(DOSSIER_SESSIONS).resolve(idSession);
    }

    /**
     * Lit le meta et verifie que la session appartient bien a l'appelant.
     *
     * Renvoie 404 et non 403 pour une session qui n'est pas la sienne : un 403
     * confirmerait que l'identifiant existe.
     */
    private Properties metaVerifiee(String email, String idSession) {
        Path fichier = dossierSession(idSession).resolve(FICHIER_META);
        if (!Files.isReadable(fichier)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session introuvable");
        }

        Properties meta = new Properties();
        try (var entree = Files.newInputStream(fichier)) {
            meta.load(entree);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Session illisible : " + e.getMessage());
        }

        if (!email.equals(meta.getProperty("proprietaire"))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session introuvable");
        }
        return meta;
    }

    private int morceauxAttendus(long tailleTotale) {
        return (int) ((tailleTotale + tailleMorceau - 1) / tailleMorceau);
    }

    /** Six chiffres : l'ordre alphabetique des noms est l'ordre des indices. */
    private String nomMorceau(int indice) {
        return String.format("%06d", indice);
    }

    private List<Integer> morceauxPresents(String idSession) {
        Path session = dossierSession(idSession);
        List<Integer> presents = new ArrayList<>();
        try (Stream<Path> fichiers = Files.list(session)) {
            fichiers.map(p -> p.getFileName().toString())
                    .filter(nom -> nom.matches("\\d{6}"))
                    .map(Integer::parseInt)
                    .sorted()
                    .forEach(presents::add);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Session illisible : " + e.getMessage());
        }
        return presents;
    }

    private void supprimerDossier(Path racine) throws IOException {
        try (Stream<Path> contenu = Files.walk(racine)) {
            // Ordre inverse : les fichiers avant les dossiers qui les contiennent.
            for (Path p : contenu.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(p);
            }
        }
    }

    private long enMegaoctets(long octets) {
        return octets / (1024L * 1024L);
    }
}
