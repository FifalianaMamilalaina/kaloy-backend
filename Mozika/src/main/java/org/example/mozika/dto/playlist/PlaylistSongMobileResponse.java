package org.example.mozika.dto.playlist;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistSongMobileResponse {
    private Long playlistSongId;
    private Long songId;
    private String title;
    private String artistName;
    private Integer durationSeconds;
    private String coverUrl;
    private int position;
    private String addedAt;

    public PlaylistSongMobileResponse(Long playlistSongId, Long songId, String title,
            String artistName, Integer durationSeconds, String coverUrl,
            int position, String addedAt) {
        this.playlistSongId = playlistSongId;
        this.songId = songId;
        this.title = title;
        this.artistName = artistName;
        this.durationSeconds = durationSeconds;
        this.coverUrl = coverUrl;
        this.position = position;
        this.addedAt = addedAt;
    }
}
