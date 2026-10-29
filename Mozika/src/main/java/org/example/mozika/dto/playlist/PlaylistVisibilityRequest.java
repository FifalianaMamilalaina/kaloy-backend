package org.example.mozika.dto.playlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistVisibilityRequest {

	@NotBlank
	@Pattern(regexp = "PRIVATE|PUBLIC", message = "Visibility must be PRIVATE or PUBLIC")
	private String visibility;
}
