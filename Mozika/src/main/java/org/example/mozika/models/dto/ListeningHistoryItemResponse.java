package org.example.mozika.models.dto;

import java.time.LocalDateTime;

public record ListeningHistoryItemResponse(
        Long id,
        LocalDateTime listenedAt,
        Integer durationListenedSeconds,
        Boolean completed,
        Long songId,
        String songTitle,
        Integer songDurationSeconds,
        String artistName
) {
}
