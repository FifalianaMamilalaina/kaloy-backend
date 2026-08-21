package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.User;

import org.example.mozika.models.Song;

import org.example.mozika.models.PlayMode;



@Getter @Setter
public class ListeningHistorySearch {

	private Long id;

	private User useridUsers;
	
	private Song songidSongs;
	
	private PlayMode playmodeidPlayModes;
	
	private java.time.LocalDateTime listenedAt;
	private java.time.LocalDateTime listenedAtMin;
	private java.time.LocalDateTime listenedAtMax;

	
}
