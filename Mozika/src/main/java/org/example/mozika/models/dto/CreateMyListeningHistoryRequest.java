package org.example.mozika.models.dto;

import jakarta.validation.constraints.NotNull;

public record CreateMyListeningHistoryRequest(
    @NotNull(message = "songId est requis") Long songId,
    @NotNull(message = "playModeId est requis") Long playModeId,
    Integer durationListenedSeconds,
    Boolean completed
) {}
