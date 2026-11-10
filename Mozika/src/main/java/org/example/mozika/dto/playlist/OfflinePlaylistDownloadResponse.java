package org.example.mozika.dto.playlist;

import java.time.LocalDateTime;
import java.util.List;

public record OfflinePlaylistDownloadResponse(
        Long playlistId,
        String playlistName,
        LocalDateTime downloadedAt,
        LocalDateTime expiresAt,
        boolean active,
        List<OfflineSongDownloadResponse> songs) {
}
