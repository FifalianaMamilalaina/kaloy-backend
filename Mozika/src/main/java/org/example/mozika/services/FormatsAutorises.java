package org.example.mozika.services;

import java.util.Locale;
import java.util.Map;

/**
 * Formats de fichier acceptes par le serveur, et leur extension.
 *
 * Extrait d'UploadController parce que l'envoi par morceaux doit appliquer
 * exactement les memes regles : deux listes separees auraient fini par
 * divergeur, et un format accepte d'un cote mais pas de l'autre produit une
 * erreur au tout dernier appel, apres plusieurs minutes d'envoi.
 *
 * On se fie au type declare par le client, mais on ne garde que les formats
 * connus : cela evite d'ecrire sur le disque un nom ou une extension
 * arbitraires.
 */
public final class FormatsAutorises {

    /**
     * Une seule extension par type. Images pour les pochettes et la galerie ;
     * audio pour les chansons declarees par les artistes et pour le pipeline
     * d'ingestion (Sprint 8-9) ; video et PDF pour le pipeline.
     */
    private static final Map<String, String> EXTENSIONS = Map.ofEntries(
            Map.entry("image/jpeg", ".jpg"),
            Map.entry("image/png", ".png"),
            Map.entry("image/webp", ".webp"),
            Map.entry("audio/mpeg", ".mp3"),
            Map.entry("audio/mp3", ".mp3"),
            // M4A : ce que les telephones produisent et stockent le plus
            // souvent. Les deux types coexistent selon la plateforme —
            // Android annonce audio/mp4, iOS audio/x-m4a. L'absence de ces
            // deux lignes faisait refuser au dernier appel, apres l'envoi,
            // un fichier que l'artiste venait de choisir ; et la seule
            // chanson du catalogue qui porte un audio est un .m4a.
            Map.entry("audio/mp4", ".m4a"),
            Map.entry("audio/x-m4a", ".m4a"),
            Map.entry("video/mp4", ".mp4"),
            Map.entry("application/pdf", ".pdf")
    );

    /** Libelle pour les messages d'erreur, pour n'enumerer les formats qu'ici. */
    public static final String FORMATS_LISIBLES = "JPEG, PNG, WebP, MP3, M4A, MP4 ou PDF";

    private FormatsAutorises() {
    }

    /** Extension correspondante, ou null si le type n'est pas accepte. */
    public static String extensionPour(String typeMime) {
        if (typeMime == null) {
            return null;
        }
        return EXTENSIONS.get(typeMime.toLowerCase(Locale.ROOT));
    }
}
