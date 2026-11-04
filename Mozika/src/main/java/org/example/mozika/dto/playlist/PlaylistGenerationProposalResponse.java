package org.example.mozika.dto.playlist;

import java.util.List;

public record PlaylistGenerationProposalResponse(
		int proposalNumber,
		int totalDurationSeconds,
		List<PlaylistGenerationSongResponse> songs) {
}
