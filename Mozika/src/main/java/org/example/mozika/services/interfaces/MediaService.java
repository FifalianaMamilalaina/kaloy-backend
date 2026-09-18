package org.example.mozika.services.interfaces;

import io.minio.GetObjectResponse;
import io.minio.StatObjectResponse;
import org.example.mozika.models.Song;

import org.example.mozika.models.dto.MediaStreamUrlsDto;
import org.example.mozika.models.dto.SongPlayerDto;

public interface MediaService {
    Song getSong(Long songId);

    StatObjectResponse statObject(String objectName);

    GetObjectResponse openObject(String objectName);

    GetObjectResponse openObject(String objectName, long offset, long length);

    MediaStreamUrlsDto getSongStreamUrls(Long songId);

    SongPlayerDto getSongPlayerDetails(Long songId);
}
