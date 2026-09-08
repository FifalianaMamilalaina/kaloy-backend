package org.example.mozika.services;

import lombok.RequiredArgsConstructor;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.Song;
import org.example.mozika.models.dto.MediaStreamUrlsDto;
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
}
