package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaStreamUrlsDto {
    private String audioStreamUrl;
    private String videoStreamUrl;
    private String karaokeStreamUrl;
    private String playbackStreamUrl;
    private String solfaUrl;
}
