package org.example.mozika.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.mozika.dto.RestResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Reception des fichiers envoyes par l'application (photos d'evenement).
 *
 * Les fichiers sont ecrits sur le disque du serveur, dans le dossier
 * kaloy.uploads.dossier, et relus via GET /uploads/{nom} (voir WebConfig).
 * On renvoie une URL absolue : l'application la stocke telle quelle dans
 * event_media.url, qui attend une adresse deja hebergee.
 */
@RestController
@RequestMapping("/uploads")
@Tag(name = "Uploads", description = "Envoi de fichiers (photos d'evenement)")
public class UploadController {

    @Value("${kaloy.uploads.dossier}")
    private String dossier;

    @Value("${kaloy.uploads.url-publique}")
    private String urlPublique;

    /** Un seul format d'image par extension acceptee. */
    private static final Map<String, String> EXTENSIONS_AUTORISEES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    /** 25 Mo, en accord avec spring.servlet.multipart.max-file-size. */
    private static final long TAILLE_MAX = 25L * 1024L * 1024L;

    @Operation(
            summary = "Envoyer un fichier",
            description = "Recoit une image et renvoie l'URL publique permettant de la relire."
    )
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> televerser(@RequestParam("fichier") MultipartFile fichier) {
        if (fichier == null || fichier.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    RestResponse.buildErrorResponse(HttpStatus.BAD_REQUEST, "Fichier vide", null));
        }
        if (fichier.getSize() > TAILLE_MAX) {
            return ResponseEntity.badRequest().body(
                    RestResponse.buildErrorResponse(HttpStatus.BAD_REQUEST,
                            "Fichier trop volumineux (25 Mo maximum)", null));
        }

        // On se fie au type declare, mais on ne garde que les formats connus :
        // cela evite d'ecrire sur le disque un nom ou une extension arbitraires.
        String typeMime = fichier.getContentType() == null
                ? "" : fichier.getContentType().toLowerCase(Locale.ROOT);
        String extension = EXTENSIONS_AUTORISEES.get(typeMime);
        if (extension == null) {
            return ResponseEntity.badRequest().body(
                    RestResponse.buildErrorResponse(HttpStatus.BAD_REQUEST,
                            "Format non supporte : " + typeMime + " (JPEG, PNG ou WebP attendus)", null));
        }

        try {
            Path racine = Paths.get(dossier).toAbsolutePath().normalize();
            Files.createDirectories(racine);

            // Nom genere : le nom d'origine n'est jamais reutilise, ni pour le
            // chemin ni pour l'extension.
            String nom = UUID.randomUUID().toString().replace("-", "") + extension;
            Path destination = racine.resolve(nom);

            try (var flux = fichier.getInputStream()) {
                Files.copy(flux, destination, StandardCopyOption.REPLACE_EXISTING);
            }

            String url = urlPublique.endsWith("/") ? urlPublique + nom : urlPublique + "/" + nom;
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    RestResponse.buildSuccessResponse(HttpStatus.CREATED,
                            "fichier televerse avec succes", url));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    RestResponse.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Echec de l'ecriture du fichier : " + e.getMessage(), null));
        }
    }
}
