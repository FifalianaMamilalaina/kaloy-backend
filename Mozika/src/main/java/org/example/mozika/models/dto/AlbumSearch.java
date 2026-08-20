package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.Artist;



@Getter @Setter
public class AlbumSearch {

	private Long id;

	private Artist artistidArtists;
	
	private String title;
	private String coverUrl;
	private java.time.LocalDate releaseDate;
	private java.time.LocalDate releaseDateMin;
	private java.time.LocalDate releaseDateMax;

	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	
}
