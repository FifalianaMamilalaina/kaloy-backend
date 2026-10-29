package org.example.mozika.dto.playlist;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MergePlaylistsMobileRequest {

	@NotNull(message = "Source playlist IDs cannot be null")
	@Size(min = 2, message = "At least 2 source playlists are required")
	private List<@NotNull(message = "Source playlist ID cannot be null") Long> sourceIds;

	@NotBlank(message = "Result name cannot be blank")
	@Size(max = 150, message = "Result name must be at most 150 characters")
	private String resultName;
}
