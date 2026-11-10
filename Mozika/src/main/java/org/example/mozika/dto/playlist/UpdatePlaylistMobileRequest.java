package org.example.mozika.dto.playlist;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePlaylistMobileRequest {

	@NotBlank(message = "Name cannot be blank")
	@Size(max = 150, message = "Name must be at most 150 characters")
	private String name;

	private String description;
}
