package org.example.kaloy.ingestion.programme3;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.kaloy.ingestion.api.ClientApiKaloy;
import org.example.kaloy.ingestion.config.ConfigurationFiles;
import org.example.kaloy.ingestion.modele.LotMetadonnees;
import org.example.kaloy.ingestion.modele.MetadonneesChanson;
import org.example.kaloy.ingestion.modele.ResultatPublication;
import org.example.kaloy.ingestion.util.Textes;
import org.example.kaloy.ingestion.suivi.JournalSoumissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PROGRAMME 3 — Association au compte artiste et publication via l'API.
 *
 * Deroulement pour une archive :
 *   1. resoudre l'artiste a partir du nom de l'archive ;
 *   2. pour chaque chanson : televerser les fichiers, creer l'album si besoin,
 *      creer la chanson, rattacher le genre ;
 *   3. consigner chaque fichier dans content_submissions ;
 *   4. transmettre le resultat au Programme 4.
 *
 * Une chanson en echec n'interrompt pas les autres : l'archive est declaree
 * en echec global, donc rien ne sera supprime, mais ce qui a pu etre publie
 * l'est. C'est ce qui permet a l'artiste de corriger puis de redeposer.
 */
@Component
public class PublicationApi {

    private static final Logger log = LoggerFactory.getLogger(PublicationApi.class);

    private final ClientApiKaloy api;
    private final RabbitTemplate rabbit;
    private final JournalSoumissions journal;

    /** Genres deja resolus pendant cette execution, pour eviter des appels repetes. */
    private final Map<String, Long> cacheGenres = new HashMap<>();

    public PublicationApi(ClientApiKaloy api, RabbitTemplate rabbit, JournalSoumissions journal) {
        this.api = api;
        this.rabbit = rabbit;
        this.journal = journal;
    }

    @RabbitListener(queues = ConfigurationFiles.FILE_METADONNEES)
    public void traiter(LotMetadonnees lot) {
        log.info("{} — publication de {} chanson(s)", lot.nomArchive(), lot.chansons().size());

        List<String> erreurs = new ArrayList<>();
        int publiees = 0;

        Long idArtiste;
        try {
            idArtiste = resoudreArtiste(lot.nomArtiste());
        } catch (Exception e) {
            String motif = e.getMessage();
            log.warn("{} — {}", lot.nomArchive(), motif);
            journal.consignerEchec(lot.nomArchive(), "ARCHIVE", motif);
            envoyer(lot, false, 0, List.of(motif));
            return;
        }

        for (MetadonneesChanson chanson : lot.chansons()) {
            String nomFichier = Paths.get(chanson.cheminAudio()).getFileName().toString();
            try {
                publier(idArtiste, chanson);
                journal.consignerSucces(nomFichier, "AUDIO", idArtiste);
                publiees++;
            } catch (Exception e) {
                String motif = "« " + chanson.titre() + " » : " + messageLisible(e);
                log.warn("{} — {}", lot.nomArchive(), motif);
                journal.consignerEchec(nomFichier, "AUDIO", motif, idArtiste);
                erreurs.add(motif);
            }
        }

        boolean succes = erreurs.isEmpty() && publiees > 0;
        log.info("{} — {} publiee(s), {} en echec", lot.nomArchive(), publiees, erreurs.size());
        envoyer(lot, succes, publiees, erreurs);
    }

    private void envoyer(LotMetadonnees lot, boolean succes, int publiees, List<String> erreurs) {
        rabbit.convertAndSend(ConfigurationFiles.FILE_PUBLIES, new ResultatPublication(
                lot.nomArchive(), lot.cheminArchive(), lot.nomArtiste(),
                lot.dossierTravail(), succes, publiees, erreurs));
    }

    // ---------------- Resolution de l'artiste ----------------

    /**
     * Retrouve l'artiste a partir du nom de scene deduit de l'archive.
     *
     * La recherche du backend etant un « contient », plusieurs artistes
     * peuvent remonter : on exige une correspondance exacte apres
     * normalisation (casse et accents ignores). Si elle est ambigue,
     * l'archive part en echec plutot que d'etre rattachee au hasard.
     */
    private Long resoudreArtiste(String nomScene) {
        JsonNode reponse = api.rechercher("/artists/search?page=0&size=20",
                Map.of("stageName", nomScene));

        JsonNode contenu = reponse == null ? null : reponse.path("data").path("content");
        if (contenu == null || !contenu.isArray() || contenu.isEmpty()) {
            throw new IllegalStateException("Aucun artiste ne correspond a « " + nomScene + " »");
        }

        String recherche = Textes.normaliser(nomScene);
        List<JsonNode> exacts = new ArrayList<>();
        for (JsonNode candidat : contenu) {
            if (Textes.normaliser(candidat.path("stageName").asText()).equals(recherche)) {
                exacts.add(candidat);
            }
        }

        if (exacts.isEmpty()) {
            throw new IllegalStateException("Aucun artiste nomme exactement « " + nomScene + " »");
        }
        if (exacts.size() > 1) {
            throw new IllegalStateException(
                    "Plusieurs artistes portent le nom « " + nomScene + " » : association impossible");
        }
        return exacts.get(0).path("id").asLong();
    }

    // ---------------- Publication d'une chanson ----------------

    private void publier(Long idArtiste, MetadonneesChanson chanson) {
        if (chanson.titre() == null || chanson.titre().isBlank()) {
            throw new IllegalStateException("titre absent");
        }

        String urlAudio = api.televerser(Paths.get(chanson.cheminAudio()));
        String urlVideo = chanson.cheminVideo() == null
                ? null : api.televerser(Paths.get(chanson.cheminVideo()));
        String urlPochette = chanson.cheminPochette() == null
                ? null : api.televerser(Paths.get(chanson.cheminPochette()));

        Long idAlbum = chanson.album() == null || chanson.album().isBlank()
                ? null : creerAlbumSiBesoin(idArtiste, chanson.album(), urlPochette, chanson.dateSortie());

        Map<String, Object> corps = new LinkedHashMap<>();
        corps.put("artistidArtists", Map.of("id", idArtiste));
        if (idAlbum != null) corps.put("albumidAlbums", Map.of("id", idAlbum));
        corps.put("title", chanson.titre());
        if (chanson.dureeSecondes() != null) corps.put("durationSeconds", chanson.dureeSecondes());
        if (chanson.dateSortie() != null) corps.put("releaseDate", chanson.dateSortie());
        corps.put("language", "mg");
        // EXTERNAL_LINK : le fichier est heberge par le serveur et reference
        // par son URL, il n'est pas stocke en base.
        corps.put("storagetypeidAudioStorageTypes", Map.of("id", idTypeStockageLienExterne()));
        corps.put("audioUrl", urlAudio);
        if (urlVideo != null) corps.put("videoUrl", urlVideo);
        if (chanson.paroles() != null) corps.put("lyrics", chanson.paroles());
        corps.put("isDownloadable", true);
        corps.put("createdAt", ClientApiKaloy.maintenant());

        // Le service parcourt les cinq listes de details sans les tester :
        // une seule laissee nulle provoque une erreur 500. On les envoie donc
        // toutes, vides, meme celles qui ne nous concernent pas.
        Map<String, Object> enveloppe = new LinkedHashMap<>();
        enveloppe.put("song", corps);
        enveloppe.put("editorialPlaylistSongs", List.of());
        enveloppe.put("listeningHistorys", List.of());
        enveloppe.put("playlistSongs", List.of());
        enveloppe.put("songGenres", List.of());
        enveloppe.put("upNextQueues", List.of());

        JsonNode creee = api.post("/songs", enveloppe);
        long idChanson = extraireId(creee, "creation de la chanson");

        if (chanson.genre() != null && !chanson.genre().isBlank()) {
            rattacherGenre(idChanson, chanson.genre());
        }
    }

    private Long creerAlbumSiBesoin(Long idArtiste, String titre, String urlPochette, String dateSortie) {
        JsonNode existants = api.rechercher("/albums/search?page=0&size=20", Map.of("title", titre));
        JsonNode contenu = existants == null ? null : existants.path("data").path("content");
        if (contenu != null && contenu.isArray()) {
            for (JsonNode album : contenu) {
                boolean memeTitre = Textes.normaliser(album.path("title").asText())
                        .equals(Textes.normaliser(titre));
                boolean memeArtiste = album.path("artistidArtists").path("id").asLong() == idArtiste;
                if (memeTitre && memeArtiste) {
                    return album.path("id").asLong();
                }
            }
        }

        Map<String, Object> corps = new LinkedHashMap<>();
        corps.put("artistidArtists", Map.of("id", idArtiste));
        corps.put("title", titre);
        if (urlPochette != null) corps.put("coverUrl", urlPochette);
        if (dateSortie != null) corps.put("releaseDate", dateSortie);
        corps.put("createdAt", ClientApiKaloy.maintenant());

        return extraireId(api.post("/albums", Map.of("album", corps, "songs", List.of())), "creation de l'album");
    }

    private void rattacherGenre(long idChanson, String libelle) {
        Long idGenre = cacheGenres.computeIfAbsent(
                Textes.normaliser(libelle), cle -> resoudreGenre(libelle));

        api.post("/songgenres", Map.of(
                "songidSongs", Map.of("id", idChanson),
                "genreidGenres", Map.of("id", idGenre)));
    }

    private Long resoudreGenre(String libelle) {
        JsonNode existants = api.rechercher("/genres/search?page=0&size=20", Map.of("name", libelle));
        JsonNode contenu = existants == null ? null : existants.path("data").path("content");
        if (contenu != null && contenu.isArray()) {
            for (JsonNode genre : contenu) {
                if (Textes.normaliser(genre.path("name").asText())
                        .equals(Textes.normaliser(libelle))) {
                    return genre.path("id").asLong();
                }
            }
        }
        // Un genre inconnu est cree : le catalogue malgache en compte
        // beaucoup, et refuser l'import pour cette raison serait excessif.
        return extraireId(api.post("/genres", Map.of("genre", Map.of("name", libelle), "songGenres", List.of())), "creation du genre");
    }

    /** Le type de stockage est une table de reference ; on lit son id par son nom. */
    private long idTypeStockageLienExterne() {
        JsonNode types = api.get("/audiostoragetypes?page=0&size=20");
        JsonNode contenu = types == null ? null : types.path("data").path("content");
        if (contenu != null && contenu.isArray()) {
            for (JsonNode type : contenu) {
                if ("EXTERNAL_LINK".equals(type.path("name").asText())) {
                    return type.path("id").asLong();
                }
            }
        }
        throw new IllegalStateException("Type de stockage EXTERNAL_LINK introuvable");
    }

    private long extraireId(JsonNode reponse, String contexte) {
        if (reponse == null || !reponse.path("data").hasNonNull("id")) {
            String message = reponse == null ? "reponse vide" : reponse.path("message").asText();
            throw new IllegalStateException(contexte + " refusee (" + message + ")");
        }
        return reponse.path("data").path("id").asLong();
    }

    private String messageLisible(Exception e) {
        String message = e.getMessage();
        if (message == null) return e.getClass().getSimpleName();
        return message.length() > 300 ? message.substring(0, 300) + "…" : message;
    }
}
