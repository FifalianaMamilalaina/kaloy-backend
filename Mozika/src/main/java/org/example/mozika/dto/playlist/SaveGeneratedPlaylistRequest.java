package org.example.mozika.dto.playlist;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SaveGeneratedPlaylistRequest {
	@NotBlank
	@Size(max = 150)
	private String name;

	@Valid
	private PlaylistGenerationRequest criteria;

	@NotEmpty
	private List<Long> songIds = new ArrayList<>();
}
