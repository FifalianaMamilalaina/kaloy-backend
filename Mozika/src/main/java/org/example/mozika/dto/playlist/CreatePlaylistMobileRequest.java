package org.example.mozika.dto.playlist;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreatePlaylistMobileRequest {

	@NotBlank(message = "Name cannot be blank")
	@Size(max = 150, message = "Name must be at most 150 characters")
	private String name;

	private List<Long> genreIds = new ArrayList<>();
	private List<Long> artistIds = new ArrayList<>();
	private int durationMinutes = 30;
}
