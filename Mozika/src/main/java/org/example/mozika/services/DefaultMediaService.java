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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultMediaService implements MediaService {

    private final SongRepository songRepository;
    private final StorageService storageService;

    @Override
    public MediaStreamUrlsDto getSongStreamUrls(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song introuvable avec l'id : " + songId));

        MediaStreamUrlsDto dto = new MediaStreamUrlsDto();
        dto.setAudioStreamUrl(storageService.getPresignedUrl(song.getAudioUrl()));
        dto.setVideoStreamUrl(storageService.getPresignedUrl(song.getVideoUrl()));
        dto.setKaraokeStreamUrl(storageService.getPresignedUrl(song.getKaraokeAudioUrl()));
        dto.setPlaybackStreamUrl(storageService.getPresignedUrl(song.getPlaybackUrl()));
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

        // URLs presignées MinIO (valides 7 jours)
        dto.setAudioStreamUrl(storageService.getPresignedUrl(song.getAudioUrl()));
        dto.setVideoStreamUrl(storageService.getPresignedUrl(song.getVideoUrl()));
        dto.setKaraokeStreamUrl(storageService.getPresignedUrl(song.getKaraokeAudioUrl()));
        dto.setPlaybackStreamUrl(storageService.getPresignedUrl(song.getPlaybackUrl()));
        dto.setSolfaUrl(storageService.getPresignedUrl(song.getSolfaUrl()));

        return dto;
    }
}
