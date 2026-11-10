package org.example.mozika.dto.playlist;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlaylistGenerationRequest {
	@NotNull
	@Min(1)
	@Max(180)
	private Integer durationMinutes;

	private List<Long> includedGenreIds = new ArrayList<>();
	private List<Long> excludedGenreIds = new ArrayList<>();
	private List<Long> includedArtistIds = new ArrayList<>();
	private List<Long> excludedArtistIds = new ArrayList<>();
}
