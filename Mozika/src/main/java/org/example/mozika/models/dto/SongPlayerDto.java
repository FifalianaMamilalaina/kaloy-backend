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
