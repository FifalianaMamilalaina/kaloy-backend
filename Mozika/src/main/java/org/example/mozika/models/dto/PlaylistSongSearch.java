package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.Playlist;

import org.example.mozika.models.Song;



@Getter @Setter
public class PlaylistSongSearch {

	private Long id;

	private Playlist playlistidPlaylists;
	
	private Song songidSongs;
	
	private Integer position;
	private Integer positionMin;
	private Integer positionMax;

	private java.time.LocalDateTime addedAt;
	private java.time.LocalDateTime addedAtMin;
	private java.time.LocalDateTime addedAtMax;

	
}
