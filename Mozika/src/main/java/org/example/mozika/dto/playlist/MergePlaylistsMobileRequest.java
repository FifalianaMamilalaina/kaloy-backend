package org.example.mozika.dto.playlist;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MergePlaylistsMobileRequest {

	@NotEmpty(message = "Source playlist IDs cannot be empty")
	private List<Long> sourceIds;

	@NotBlank(message = "Result name cannot be blank")
	@Size(max = 150, message = "Result name must be at most 150 characters")
	private String resultName;
}
