package org.example.mozika.services;

import lombok.RequiredArgsConstructor;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.Album;
import org.example.mozika.models.Artist;
import org.example.mozika.models.Song;
import org.example.mozika.models.dto.MediaStreamUrlsDto;
import org.example.mozika.models.dto.SongPlayerDto;
import org.example.mozika.repositories.SongRepository;
import org.example.mozika.services.interfaces.MediaService;
import org.example.mozika.services.interfaces.StorageService;
import org.example.mozika.utils.YouTubeUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import io.minio.GetObjectResponse;
import io.minio.StatObjectResponse;

@Service
@RequiredArgsConstructor
public class DefaultMediaService implements MediaService {

    private final SongRepository songRepository;
    private final StorageService storageService;

    @Value("${app.media-base-url}")
    private String mediaBaseUrl;

    @Override
    public MediaStreamUrlsDto getSongStreamUrls(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song introuvable avec l'id : " + songId));

        MediaStreamUrlsDto dto = new MediaStreamUrlsDto();
        dto.setAudioStreamUrl(resoudreMedia(song.getAudioUrl(), songId, "audio"));
        dto.setVideoStreamUrl(resolveVideoUrl(song.getVideoUrl()));
        dto.setKaraokeStreamUrl(resoudreMedia(song.getKaraokeAudioUrl(), songId, "karaoke"));
        dto.setPlaybackStreamUrl(resoudreMedia(song.getPlaybackUrl(), songId, "playback"));
        dto.setSolfaUrl(resoudreMedia(song.getSolfaUrl(), songId, null));
        return dto;
    }

    @Override
    public SongPlayerDto getSongPlayerDetails(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song introuvable avec l'id : " + songId));

        SongPlayerDto dto = new SongPlayerDto();

        // Métadonnées de base
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        dto.setDurationSeconds(song.getDurationSeconds());
        dto.setLanguage(song.getLanguage());
        dto.setReleaseDate(song.getReleaseDate());
        dto.setIsDownloadable(song.getIsDownloadable());
        dto.setLyrics(song.getLyrics());
        dto.setLyricsSyncData(song.getLyricsSyncData());
        dto.setAuthorComposer(song.getAuthorComposer());
        dto.setMusicalArranger(song.getMusicalArranger());

        // Artiste (toujours présent — NOT NULL en BDD)
        Artist artist = song.getArtistidArtists();
        dto.setArtistId(artist.getId());
        dto.setArtistStageName(artist.getStageName());
        dto.setArtistPhotoUrl(artist.getPhotoUrl());
        dto.setArtistIsCertified(artist.getIsCertified());

        // Album (nullable — une chanson peut ne pas avoir d'album)
        Album album = song.getAlbumidAlbums();
        if (album != null) {
            dto.setAlbumId(album.getId());
            dto.setAlbumTitle(album.getTitle());
            dto.setAlbumCoverUrl(album.getCoverUrl());
            dto.setAlbumReleaseDate(album.getReleaseDate());
        }

        // Le mobile lit via le backend, sans accès direct au port MinIO.
        dto.setAudioStreamUrl(resoudreMedia(song.getAudioUrl(), songId, "audio"));
        dto.setVideoStreamUrl(resolveVideoUrl(song.getVideoUrl()));
        dto.setKaraokeStreamUrl(resoudreMedia(song.getKaraokeAudioUrl(), songId, "karaoke"));
        dto.setPlaybackStreamUrl(resoudreMedia(song.getPlaybackUrl(), songId, "playback"));
        dto.setSolfaUrl(resoudreMedia(song.getSolfaUrl(), songId, null));

        return dto;
    }

    private String resolveVideoUrl(String rawVideoUrl) {
        if (rawVideoUrl == null || rawVideoUrl.isBlank())
            return null;
        if (YouTubeUtils.isYouTubeUrl(rawVideoUrl)) {
            String videoId = YouTubeUtils.extractVideoId(rawVideoUrl);
            return videoId != null ? YouTubeUtils.getEmbedUrl(videoId) : null;
        }
        return storageService.getPresignedUrl(rawVideoUrl);
    }

    /**
     * Adresse de lecture d'un media, selon la facon dont il est stocke.
     *
     * Deux stockages coexistent dans l'application :
     *
     *   * un nom d'objet MinIO (« songs/301/audio.mp3 ») — le mobile le lit
     *     alors par /media/songs/{id}/stream/{type}, qui relaie le flux sans
     *     exposer le port MinIO ;
     *   * une URL absolue, servie par le backend lui-meme depuis son dossier
     *     uploads. C'est deja le cas des photos d'evenement et de tout ce que
     *     depose le pipeline d'ingestion, qui poste sur /uploads.
     *
     * Reconnaitre les deux evite de dependre de MinIO pour travailler, sans
     * renoncer a MinIO : le jour ou il tourne, les noms d'objets continuent d'y
     * passer. C'est le meme principe que resolveVideoUrl, qui traite deja les
     * liens YouTube a part plutot que de supposer un seul stockage.
     *
     * @param mediaType null pour un media sans route de streaming (la partition)
     */
    private String resoudreMedia(String valeurStockee, Long songId, String mediaType) {
        if (valeurStockee == null || valeurStockee.isBlank()) {
            return null;
        }
        if (estUrlAbsolue(valeurStockee) || valeurStockee.startsWith("/")) {
            // Une adresse absolue (un lien YouTube, une ancienne ligne) ou un
            // chemin (« /uploads/chanson.m4a ») sont renvoyes tels quels :
            // c'est l'application qui prefixe le chemin avec sa propre adresse
            // de base. Le nom d'hote cesse ainsi d'etre une donnee persistee.
            return valeurStockee;
        }
        if (mediaType == null) {
            return storageService.getPresignedUrl(valeurStockee);
        }
        return streamUrl(songId, mediaType);
    }

    private boolean estUrlAbsolue(String valeur) {
        String v = valeur.toLowerCase();
        return v.startsWith("http://") || v.startsWith("https://");
    }

    private String streamUrl(Long songId, String mediaType) {
        return mediaBaseUrl + "/media/songs/" + songId + "/stream/" + mediaType;
    }

    @Override
    public Song getSong(Long songId) {
        return songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song introuvable avec l'id : " + songId));
    }

    @Override
    public StatObjectResponse statObject(String objectName) {
        return storageService.statObject(objectName);
    }

    @Override
    public GetObjectResponse openObject(String objectName) {
        return storageService.getObject(objectName);
    }

    @Override
    public GetObjectResponse openObject(String objectName, long offset, long length) {
        return storageService.getObject(objectName, offset, length);
    }
}
