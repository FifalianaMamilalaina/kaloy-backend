package org.example.kaloy.ingestion.programme2;

import org.example.kaloy.ingestion.config.ConfigurationFiles;
import org.example.kaloy.ingestion.modele.ArchiveExtraite;
import org.example.kaloy.ingestion.modele.FichierExtrait;
import org.example.kaloy.ingestion.modele.LotMetadonnees;
import org.example.kaloy.ingestion.modele.MetadonneesChanson;
import org.example.kaloy.ingestion.modele.TypeFichier;
import org.example.kaloy.ingestion.suivi.JournalSoumissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.example.kaloy.ingestion.util.Textes;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * PROGRAMME 2 — Extraction des metadonnees et reconciliation des sources.
 *
 * Trois sources possibles, par ordre de priorite :
 *
 *   1. le fichier Excel, s'il est fourni — c'est une saisie deliberee de
 *      l'artiste, donc plus fiable qu'un tag ;
 *   2. les tags ID3 du MP3 ;
 *   3. le nom du fichier, en dernier recours.
 *
 * Les paroles sont rattachees par la convention de nommage
 * NomArtiste_TitreChanson_paroles.pdf.
 */
@Component
public class ExtracteurMetadonnees {

    private static final Logger log = LoggerFactory.getLogger(ExtracteurMetadonnees.class);

    private final LecteurId3 lecteurId3;
    private final LecteurExcel lecteurExcel;
    private final LecteurParolesPdf lecteurParoles;
    private final RabbitTemplate rabbit;
    private final JournalSoumissions journal;

    public ExtracteurMetadonnees(LecteurId3 lecteurId3,
                                 LecteurExcel lecteurExcel,
                                 LecteurParolesPdf lecteurParoles,
                                 RabbitTemplate rabbit,
                                 JournalSoumissions journal) {
        this.lecteurId3 = lecteurId3;
        this.lecteurExcel = lecteurExcel;
        this.lecteurParoles = lecteurParoles;
        this.rabbit = rabbit;
        this.journal = journal;
    }

    @RabbitListener(queues = ConfigurationFiles.FILE_EXTRAITS)
    public void traiter(ArchiveExtraite archive) {
        log.info("{} — lecture des metadonnees", archive.nomArchive());

        // Le tableur, s'il existe, est indexe par titre normalise.
        Map<String, LecteurExcel.LigneExcel> parTitre = archive.fichiers().stream()
                .filter(f -> f.type() == TypeFichier.TABLEUR)
                .findFirst()
                .map(f -> lecteurExcel.lire(Paths.get(f.chemin())))
                .orElse(Map.of());

        List<FichierExtrait> audios = archive.fichiers().stream()
                .filter(f -> f.type() == TypeFichier.AUDIO)
                .toList();

        if (audios.isEmpty() && parTitre.isEmpty()) {
            String motif = "Aucun fichier audio ni tableur de metadonnees dans l'archive";
            log.warn("{} — {}", archive.nomArchive(), motif);
            journal.consignerEchec(archive.nomArchive(), "ARCHIVE", motif);
            return;
        }

        List<MetadonneesChanson> chansons = new ArrayList<>();
        for (FichierExtrait audio : audios) {
            chansons.add(construire(archive, audio, parTitre));
        }

        if (chansons.isEmpty()) {
            String motif = "Le tableur decrit des chansons mais aucun fichier audio n'est fourni";
            journal.consignerEchec(archive.nomArchive(), "ARCHIVE", motif);
            log.warn("{} — {}", archive.nomArchive(), motif);
            return;
        }

        rabbit.convertAndSend(ConfigurationFiles.FILE_METADONNEES, new LotMetadonnees(
                archive.nomArchive(),
                archive.cheminArchive(),
                archive.nomArtiste(),
                archive.dossierTravail(),
                chansons
        ));
        log.info("{} — {} chanson(s) decrite(s), transmises au programme 3",
                archive.nomArchive(), chansons.size());
    }

    private MetadonneesChanson construire(ArchiveExtraite archive,
                                          FichierExtrait audio,
                                          Map<String, LecteurExcel.LigneExcel> parTitre) {
        Path chemin = Paths.get(audio.chemin());
        LecteurId3.TagsId3 tags = lecteurId3.lire(chemin);

        String titreId3 = tags == null ? null : tags.titre();
        String titreParDefaut = Textes.sansExtension(audio.nomOriginal());
        String titreProvisoire = premierRenseigne(titreId3, titreParDefaut);

        // Reconciliation : l'Excel prime sur le tag, le tag prime sur le nom.
        LecteurExcel.LigneExcel ligne = parTitre.get(Textes.normaliser(titreProvisoire));
        if (ligne == null && parTitre.size() == 1) {
            // Cas courant : un seul titre decrit, un seul MP3. On accepte
            // l'appariement meme si les libelles different legerement.
            ligne = parTitre.values().iterator().next();
        }

        String titre = premierRenseigne(ligne == null ? null : ligne.titre(), titreProvisoire);
        String album = premierRenseigne(
                ligne == null ? null : ligne.album(),
                tags == null ? null : tags.album());
        String genre = premierRenseigne(
                ligne == null ? null : ligne.genre(),
                tags == null ? null : tags.genre());
        String dateSortie = ligne == null ? null : ligne.dateSortie();
        Integer duree = tags == null ? null : tags.dureeSecondes();

        String pochette = archive.fichiers().stream()
                .filter(f -> f.type() == TypeFichier.IMAGE)
                .map(FichierExtrait::chemin)
                .findFirst().orElse(null);

        String video = archive.fichiers().stream()
                .filter(f -> f.type() == TypeFichier.VIDEO)
                .map(FichierExtrait::chemin)
                .findFirst().orElse(null);

        String paroles = trouverParoles(archive, titre);

        return new MetadonneesChanson(
                titre, album, genre, dateSortie, duree,
                audio.chemin(), video, pochette, paroles
        );
    }

    /**
     * Rattache le PDF de paroles par la convention
     * NomArtiste_TitreChanson_paroles.pdf : on compare la partie centrale du
     * nom au titre de la chanson, accents et casse ignores.
     */
    private String trouverParoles(ArchiveExtraite archive, String titre) {
        Optional<FichierExtrait> candidat = archive.fichiers().stream()
                .filter(f -> f.type() == TypeFichier.PAROLES)
                .filter(f -> correspondAuTitre(f.nomOriginal(), titre))
                .findFirst();

        if (candidat.isEmpty()) {
            return null;
        }
        return lecteurParoles.lire(Paths.get(candidat.get().chemin()));
    }

    static boolean correspondAuTitre(String nomPdf, String titre) {
        String sansExt = Textes.sansExtension(nomPdf);
        String normalise = Textes.normaliser(sansExt);
        // On retire le suffixe conventionnel avant de comparer.
        normalise = normalise.replace("paroles", "");
        return normalise.contains(Textes.normaliser(titre));
    }



    private static String premierRenseigne(String... valeurs) {
        for (String valeur : valeurs) {
            if (valeur != null && !valeur.isBlank()) {
                return valeur.trim();
            }
        }
        return null;
    }
}
