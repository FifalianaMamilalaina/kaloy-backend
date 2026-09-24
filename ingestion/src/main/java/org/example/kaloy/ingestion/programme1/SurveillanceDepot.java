package org.example.kaloy.ingestion.programme1;

import org.example.kaloy.ingestion.config.ConfigurationFiles;
import org.example.kaloy.ingestion.config.ProprietesIngestion;
import org.example.kaloy.ingestion.modele.ArchiveExtraite;
import org.example.kaloy.ingestion.suivi.JournalSoumissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

/**
 * PROGRAMME 1 — Surveillance du dossier de depot et extraction des archives.
 *
 * Le dossier est scrute a intervalle regulier plutot que par WatchService :
 * un evenement de creation de fichier survient des le debut de l'ecriture,
 * alors qu'une archive copiee depuis le reseau met du temps a arriver. Le
 * scan periodique, combine au controle de stabilite de taille, evite
 * d'ouvrir une archive incomplete.
 */
@Component
public class SurveillanceDepot {

    private static final Logger log = LoggerFactory.getLogger(SurveillanceDepot.class);

    private final ProprietesIngestion proprietes;
    private final ExtracteurArchive extracteur;
    private final RabbitTemplate rabbit;
    private final JournalSoumissions journal;

    /**
     * Archives deja prises en charge pendant cette execution. Sans cette
     * memoire, le scan suivant re-traiterait une archive en echec restee
     * dans le depot, en boucle.
     */
    private final Set<String> dejaVues = new HashSet<>();

    /** Taille relevee au scan precedent, pour detecter une copie en cours. */
    private final java.util.Map<String, Long> taillesPrecedentes = new java.util.HashMap<>();

    public SurveillanceDepot(ProprietesIngestion proprietes,
                             ExtracteurArchive extracteur,
                             RabbitTemplate rabbit,
                             JournalSoumissions journal) {
        this.proprietes = proprietes;
        this.extracteur = extracteur;
        this.rabbit = rabbit;
        this.journal = journal;
    }

    @Scheduled(fixedDelayString = "${kaloy.ingestion.intervalle-scan-ms}")
    public void scruter() {
        Path depot = Paths.get(proprietes.getDepot()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(depot);
        } catch (IOException e) {
            log.error("Dossier de depot inaccessible : {}", e.getMessage());
            return;
        }

        try (Stream<Path> fichiers = Files.list(depot)) {
            fichiers.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".zip"))
                    .forEach(this::traiterSiPrete);
        } catch (IOException e) {
            log.error("Lecture du depot impossible : {}", e.getMessage());
        }
    }

    private void traiterSiPrete(Path archive) {
        String nom = archive.getFileName().toString();
        if (dejaVues.contains(nom)) {
            return;
        }

        // Une archive dont la taille change encore est en cours de copie.
        long taille;
        try {
            taille = Files.size(archive);
        } catch (IOException e) {
            return;
        }
        Long precedente = taillesPrecedentes.put(nom, taille);
        if (precedente == null || precedente != taille) {
            log.debug("Archive {} encore en cours d'ecriture, on attend", nom);
            return;
        }

        dejaVues.add(nom);
        traiter(archive, nom);
    }

    private void traiter(Path archive, String nom) {
        log.info("Nouvelle archive detectee : {}", nom);

        String nomArtiste = deduireNomArtiste(nom);
        if (nomArtiste.isBlank()) {
            String motif = "Nom d'archive invalide : le nom de scene doit preceder le premier « _ » "
                    + "(exemple : Rossy_TsyVery.zip)";
            log.warn("{} — {}", nom, motif);
            journal.consignerEchec(nom, "ARCHIVE", motif);
            return;
        }

        try {
            ArchiveExtraite extraite = extracteur.extraire(archive, nomArtiste);

            if (extraite.fichiers().isEmpty()) {
                String motif = "Archive vide ou ne contenant aucun fichier exploitable";
                journal.consignerEchec(nom, "ARCHIVE", motif);
                log.warn("{} — {}", nom, motif);
                return;
            }

            rabbit.convertAndSend(ConfigurationFiles.FILE_EXTRAITS, extraite);
            log.info("{} — {} fichier(s) extrait(s), transmis au programme 2",
                    nom, extraite.fichiers().size());

        } catch (Exception e) {
            String motif = "Extraction impossible : " + e.getMessage();
            log.error("{} — {}", nom, motif);
            journal.consignerEchec(nom, "ARCHIVE", motif);
            // L'archive reste dans le depot : elle est corrigible.
        }
    }

    /**
     * Le nom de scene est ce qui precede le premier « _ », par coherence avec
     * la convention de nommage des paroles (NomArtiste_TitreChanson_paroles.pdf).
     *
     * « Rossy_TsyVery.zip » -> « Rossy »
     * « Mage 4_Tantara.zip » -> « Mage 4 »
     */
    static String deduireNomArtiste(String nomArchive) {
        String sansExtension = nomArchive.replaceAll("(?i)\\.zip$", "");
        int separateur = sansExtension.indexOf('_');
        String candidat = separateur > 0 ? sansExtension.substring(0, separateur) : sansExtension;
        return candidat.trim();
    }

    /** Utilise par les tests : liste des types acceptes. */
    static List<String> extensionsArchive() {
        return List.of(".zip");
    }
}
