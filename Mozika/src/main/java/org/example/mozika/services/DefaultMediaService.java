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

    @Value("${app.media-base-url:http://172.16.0.26:8087/mozika}")
    private String mediaBaseUrl;

    @Override
    public MediaStreamUrlsDto getSongStreamUrls(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song introuvable avec l'id : " + songId));

        MediaStreamUrlsDto dto = new MediaStreamUrlsDto();
        dto.setAudioStreamUrl(streamUrl(songId, "audio"));
        dto.setVideoStreamUrl(resolveVideoUrl(song.getVideoUrl()));
        dto.setKaraokeStreamUrl(streamUrl(songId, "karaoke"));
        dto.setPlaybackStreamUrl(streamUrl(songId, "playback"));
        dto.setSolfaUrl(storageService.getPresignedUrl(song.getSolfaUrl()));
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
        dto.setAudioStreamUrl(streamUrl(songId, "audio"));
        dto.setVideoStreamUrl(resolveVideoUrl(song.getVideoUrl()));
        dto.setKaraokeStreamUrl(streamUrl(songId, "karaoke"));
        dto.setPlaybackStreamUrl(streamUrl(songId, "playback"));
        dto.setSolfaUrl(storageService.getPresignedUrl(song.getSolfaUrl()));

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
