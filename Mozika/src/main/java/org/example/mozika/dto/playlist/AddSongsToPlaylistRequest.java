package org.example.mozika.dto.playlist;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddSongsToPlaylistRequest {
    @NotNull(message = "Song IDs cannot be null")
    @NotEmpty(message = "At least one song ID is required")
    private List<Long> songIds;
}
