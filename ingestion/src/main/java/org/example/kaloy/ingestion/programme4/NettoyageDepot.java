package org.example.kaloy.ingestion.programme4;

import org.example.kaloy.ingestion.config.ConfigurationFiles;
import org.example.kaloy.ingestion.modele.ResultatPublication;
import org.example.kaloy.ingestion.notification.NotificateurEchec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * PROGRAMME 4 — Nettoyage du repertoire de depot.
 *
 * Regle centrale du cahier des charges : **seuls les fichiers traites avec
 * succes sont supprimes**. Une archive en echec reste dans le depot, visible
 * et corrigible, plutot que d'etre silencieusement perdue.
 *
 * Le dossier de travail, lui, est toujours supprime : ce n'est qu'une copie
 * de travail, et la conserver ne sert a rien. L'original reste dans le depot
 * quand quelque chose a echoue.
 */
@Component
public class NettoyageDepot {

    private static final Logger log = LoggerFactory.getLogger(NettoyageDepot.class);

    private final NotificateurEchec notificateur;

    public NettoyageDepot(NotificateurEchec notificateur) {
        this.notificateur = notificateur;
    }

    @RabbitListener(queues = ConfigurationFiles.FILE_PUBLIES)
    public void traiter(ResultatPublication resultat) {
        if (resultat.succes()) {
            supprimerArchive(resultat);
        } else {
            log.warn("{} — archive conservee dans le depot ({} erreur(s)) : {}",
                    resultat.nomArchive(), resultat.erreurs().size(), resultat.erreurs());
            notificateur.signaler(resultat);
        }

        // Dans les deux cas, la copie de travail n'a plus d'utilite.
        supprimerDossierTravail(resultat.dossierTravail());
    }

    private void supprimerArchive(ResultatPublication resultat) {
        Path archive = Paths.get(resultat.cheminArchive());
        try {
            boolean supprimee = Files.deleteIfExists(archive);
            if (supprimee) {
                log.info("{} — {} chanson(s) publiee(s), archive retiree du depot",
                        resultat.nomArchive(), resultat.nombrePubliees());
            } else {
                log.warn("{} — archive introuvable au moment du nettoyage", resultat.nomArchive());
            }
        } catch (IOException e) {
            // Le contenu est en base : l'import a reussi. Une archive non
            // supprimee sera simplement reproposee au prochain scan.
            log.error("{} — suppression impossible : {}", resultat.nomArchive(), e.getMessage());
        }
    }

    private void supprimerDossierTravail(String dossier) {
        if (dossier == null || dossier.isBlank()) return;
        Path racine = Paths.get(dossier);
        if (!Files.exists(racine)) return;

        try (Stream<Path> chemins = Files.walk(racine)) {
            // Ordre inverse : les fichiers avant les dossiers qui les contiennent.
            chemins.sorted(Comparator.reverseOrder()).forEach(chemin -> {
                try {
                    Files.deleteIfExists(chemin);
                } catch (IOException e) {
                    log.debug("Nettoyage partiel, {} conserve : {}", chemin, e.getMessage());
                }
            });
        } catch (IOException e) {
            log.debug("Nettoyage du dossier de travail incomplet : {}", e.getMessage());
        }
    }
}
