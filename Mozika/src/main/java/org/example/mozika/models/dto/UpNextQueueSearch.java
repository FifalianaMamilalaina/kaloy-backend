package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.User;

import org.example.mozika.models.Song;



@Getter @Setter
public class UpNextQueueSearch {

	private Long id;

	private User useridUsers;
	
	private Song songidSongs;
	
	private Integer position;
	private Integer positionMin;
	private Integer positionMax;

	private java.time.LocalDateTime addedAt;
	private java.time.LocalDateTime addedAtMin;
	private java.time.LocalDateTime addedAtMax;

	
}
