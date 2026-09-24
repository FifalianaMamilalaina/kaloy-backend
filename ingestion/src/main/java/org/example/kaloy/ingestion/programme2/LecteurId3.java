package org.example.kaloy.ingestion.programme2;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Lecture des tags ID3 d'un MP3, via jaudiotagger comme prevu au cahier
 * des charges.
 *
 * Un MP3 sans tag n'est pas une erreur : beaucoup d'enregistrements bruts
 * n'en portent pas. On renvoie alors null, et le Programme 2 se rabat sur le
 * tableur ou sur le nom du fichier.
 */
@Component
public class LecteurId3 {

    private static final Logger log = LoggerFactory.getLogger(LecteurId3.class);

    static {
        // jaudiotagger journalise abondamment en INFO, y compris des traces
        // d'analyse de trames : on le fait taire.
        java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(java.util.logging.Level.WARNING);
    }

    public record TagsId3(String titre, String artiste, String album, String genre, Integer dureeSecondes) {
    }

    public TagsId3 lire(Path fichier) {
        try {
            AudioFile audio = AudioFileIO.read(fichier.toFile());
            Tag tag = audio.getTag();

            Integer duree = audio.getAudioHeader() == null
                    ? null : audio.getAudioHeader().getTrackLength();

            if (tag == null) {
                return new TagsId3(null, null, null, null, duree);
            }

            return new TagsId3(
                    valeur(tag, FieldKey.TITLE),
                    valeur(tag, FieldKey.ARTIST),
                    valeur(tag, FieldKey.ALBUM),
                    valeur(tag, FieldKey.GENRE),
                    duree
            );
        } catch (Exception e) {
            log.debug("Tags ID3 illisibles pour {} : {}", fichier.getFileName(), e.getMessage());
            return null;
        }
    }

    private String valeur(Tag tag, FieldKey cle) {
        try {
            String v = tag.getFirst(cle);
            return (v == null || v.isBlank()) ? null : v.trim();
        } catch (Exception e) {
            return null;
        }
    }
}
