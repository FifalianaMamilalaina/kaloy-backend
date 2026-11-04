package org.example.mozika.dto.playlist;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReorderSongItem {
    @NotNull(message = "Song ID cannot be null")
    private Long songId;

    @NotNull(message = "Position cannot be null")
    @Min(value = 0, message = "Position must be >= 0")
    private Integer position;
}
