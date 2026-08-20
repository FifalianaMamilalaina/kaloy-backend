package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.Song;

import org.example.mozika.models.Genre;



@Getter @Setter
public class SongGenreSearch {

	private Long id;

	private Song songidSongs;
	
	private Genre genreidGenres;
	
	
}
