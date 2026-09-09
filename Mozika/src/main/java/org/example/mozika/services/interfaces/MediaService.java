package org.example.mozika.services.interfaces;

import org.example.mozika.models.dto.MediaStreamUrlsDto;
import org.example.mozika.models.dto.SongPlayerDto;

public interface MediaService {
    MediaStreamUrlsDto getSongStreamUrls(Long songId);
    SongPlayerDto getSongPlayerDetails(Long songId);
}
