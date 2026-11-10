package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SongPlayerDto {

    // Informations de base
    private Long id;
    private String title;
    private Integer durationSeconds;
    private String language;
    private LocalDate releaseDate;
    private Boolean isDownloadable;
    private String lyrics;
    private String lyricsSyncData;

    /**
     * Le solfa, en TEXTE.
     *
     * A ne pas confondre avec solfaUrl plus bas, qui est reservee a un envoi
     * de fichier qui n'existe pas encore. Le solfa saisi par l'artiste est du
     * texte et vit dans la colonne solfa -- voir l'annexe 1.16, ou il partait
     * dans solfa_url, un varchar(255), et aurait fait echouer l'insertion au
     * premier usage reel.
     *
     * Le champ manquait ici, donc le solfa n'atteignait jamais le mobile : la
     * fiche de chanson proposait un onglet Solfa qui ouvrait solfaUrl comme un
     * lien, c'est-a-dire rien.
     */
    private String solfa;
    private String authorComposer;
    private String musicalArranger;

    // Artiste (aplati — NOT NULL en BDD)
    private Long artistId;
    private String artistStageName;
    private String artistPhotoUrl;
    private Boolean artistIsCertified;

    // Album (aplati — nullable, une chanson peut ne pas avoir d'album)
    private Long albumId;
    private String albumTitle;
    private String albumCoverUrl;
    private LocalDate albumReleaseDate;

    // URLs de streaming presignées MinIO (valides 7 jours)
    private String audioStreamUrl;
    private String videoStreamUrl;
    private String karaokeStreamUrl;
    private String playbackStreamUrl;
    private String solfaUrl;
}
