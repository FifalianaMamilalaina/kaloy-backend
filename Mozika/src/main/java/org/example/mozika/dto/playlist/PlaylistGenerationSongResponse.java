package org.example.mozika.dto.playlist;

public record PlaylistGenerationSongResponse(
		Long id,
		String title,
		String artistName,
		Integer durationSeconds,
		String coverUrl) {
}
