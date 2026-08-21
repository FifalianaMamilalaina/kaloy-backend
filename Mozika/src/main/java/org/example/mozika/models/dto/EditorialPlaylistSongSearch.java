package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.EditorialPlaylist;

import org.example.mozika.models.Song;



@Getter @Setter
public class EditorialPlaylistSongSearch {

	private Long id;

	private EditorialPlaylist editorialplaylistidEditorialPlaylists;
	
	private Song songidSongs;
	
	private Integer position;
	private Integer positionMin;
	private Integer positionMax;

	
}
