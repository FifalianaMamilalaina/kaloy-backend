package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.User;

import org.example.mozika.models.Playlist;



@Getter @Setter
public class DownloadSearch {

	private Long id;

	private User useridUsers;
	
	private Playlist playlistidPlaylists;
	
	private java.time.LocalDateTime downloadedAt;
	private java.time.LocalDateTime downloadedAtMin;
	private java.time.LocalDateTime downloadedAtMax;

	private java.time.LocalDateTime expiresAt;
	private java.time.LocalDateTime expiresAtMin;
	private java.time.LocalDateTime expiresAtMax;

	
}
