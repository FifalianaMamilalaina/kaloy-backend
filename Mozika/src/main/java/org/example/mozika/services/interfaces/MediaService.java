package org.example.mozika.services.interfaces;

import org.example.mozika.models.dto.MediaStreamUrlsDto;

public interface MediaService {
    MediaStreamUrlsDto getSongStreamUrls(Long songId);
}
