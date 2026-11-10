package org.example.mozika.dto.playlist;

public record OfflineSongDownloadResponse(
        Long songId,
        String title,
        String artistName,
        Integer durationSeconds,
        int position,
        String downloadUrl) {
}
