package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.Artist;



@Getter @Setter
public class EditorialPlaylistSearch {

	private Long id;

	private Artist artistidArtists;
	
	private String title;
	private String description;
	private String coverUrl;
	private Boolean isFeatured;
	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	
}
