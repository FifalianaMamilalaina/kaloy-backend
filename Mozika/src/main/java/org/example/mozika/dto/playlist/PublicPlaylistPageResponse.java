package org.example.mozika.dto.playlist;

import java.util.List;

import lombok.Getter;

@Getter
public class PublicPlaylistPageResponse {
	private final List<PlaylistUiItemResponse> content;
	private final int number;
	private final int size;
	private final int totalPages;
	private final long totalElements;
	private final boolean last;

	public PublicPlaylistPageResponse(List<PlaylistUiItemResponse> content, int number, int size,
			int totalPages, long totalElements, boolean last) {
		this.content = content;
		this.number = number;
		this.size = size;
		this.totalPages = totalPages;
		this.totalElements = totalElements;
		this.last = last;
	}
}
