package org.example.kaloy.ingestion.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.kaloy.ingestion.config.ProprietesIngestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

/**
 * Acces a l'API Kaloy.
 *
 * Le pipeline passe par l'API plutot que d'ecrire en base : toutes les
 * validations du backend s'appliquent alors a un import comme a une saisie
 * manuelle, et la logique metier n'est pas dupliquee.
 *
 * Le jeton est obtenu une fois puis reutilise ; il est redemande
 * automatiquement si le serveur repond 401 ou 403.
 */
@Component
public class ClientApiKaloy {

    private static final Logger log = LoggerFactory.getLogger(ClientApiKaloy.class);
    private static final DateTimeFormatter HORODATAGE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final RestClient client;
    private final ProprietesIngestion proprietes;
    private String jeton;

    public ClientApiKaloy(ProprietesIngestion proprietes, RestClient.Builder constructeur) {
        this.proprietes = proprietes;
        this.client = constructeur.baseUrl(proprietes.getApi().getBaseUrl()).build();
    }

    public static String maintenant() {
        return LocalDateTime.now().format(HORODATAGE);
    }

    // ---------------- Authentification ----------------

    private synchronized String jeton() {
        if (jeton != null) {
            return jeton;
        }
        JsonNode reponse = client.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "email", proprietes.getApi().getEmail(),
                        "password", proprietes.getApi().getMotDePasse()))
                .retrieve()
                .body(JsonNode.class);

        if (reponse == null || !reponse.path("data").hasNonNull("token")) {
            throw new IllegalStateException(
                    "Connexion au compte de service impossible : verifiez kaloy.ingestion.api");
        }
        jeton = reponse.path("data").path("token").asText();
        log.info("Compte de service connecte : {}", proprietes.getApi().getEmail());
        return jeton;
    }

    /** Force une reconnexion au prochain appel (jeton expire ou refuse). */
    public synchronized void oublierJeton() {
        jeton = null;
    }

    // ---------------- Appels ----------------

    public JsonNode get(String chemin) {
        try {
            return getInterne(chemin);
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            // Jeton expire : le pipeline tourne en continu, bien au-dela de la
            // duree de validite. On se reconnecte et on rejoue une fois.
            oublierJeton();
            return getInterne(chemin);
        }
    }

    public JsonNode post(String chemin, Object corps) {
        try {
            return postInterne(chemin, corps);
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            oublierJeton();
            return postInterne(chemin, corps);
        }
    }

    /**
     * Recherche toleree au resultat vide.
     *
     * Le backend renvoie 404 « No ... found » quand une recherche ne ramene
     * rien, la ou une page vide serait attendue. Pour une recherche, c'est une
     * reponse normale : elle ne doit pas etre traitee comme une panne.
     *
     * @return null si aucun resultat
     */
    public JsonNode rechercher(String chemin, Object corps) {
        try {
            return post(chemin, corps);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }

    private JsonNode getInterne(String chemin) {
        return client.get().uri(chemin)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jeton())
                .retrieve()
                .body(JsonNode.class);
    }

    private JsonNode postInterne(String chemin, Object corps) {
        return client.post().uri(chemin)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jeton())
                .contentType(MediaType.APPLICATION_JSON)
                .body(corps)
                .retrieve()
                .body(JsonNode.class);
    }

    /**
     * Televerse un fichier et renvoie son URL publique.
     *
     * Le type MIME est deduit de l'extension : le backend n'accepte qu'une
     * liste fermee de formats et refuse tout le reste.
     */
    public String televerser(Path fichier) {
        MultiValueMap<String, Object> formulaire = new LinkedMultiValueMap<>();
        formulaire.add("fichier", new FileSystemResource(fichier.toFile()) {
            @Override
            public String getFilename() {
                return fichier.getFileName().toString();
            }
        });

        JsonNode reponse = client.post()
                .uri("/uploads")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jeton())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(formulaire)
                .retrieve()
                .body(JsonNode.class);

        if (reponse == null || !reponse.hasNonNull("data")) {
            throw new IllegalStateException("Envoi du fichier refuse : "
                    + (reponse == null ? "reponse vide" : reponse.path("message").asText()));
        }
        return reponse.path("data").asText();
    }

    public static String typeMimeDe(String nomFichier) {
        String nom = nomFichier.toLowerCase(Locale.ROOT);
        if (nom.endsWith(".mp3")) return "audio/mpeg";
        if (nom.endsWith(".mp4")) return "video/mp4";
        if (nom.endsWith(".png")) return "image/png";
        if (nom.endsWith(".pdf")) return "application/pdf";
        return "image/jpeg";
    }
}
