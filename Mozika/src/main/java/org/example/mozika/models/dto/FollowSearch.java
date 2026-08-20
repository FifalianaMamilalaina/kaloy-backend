package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.User;

import org.example.mozika.models.Artist;



@Getter @Setter
public class FollowSearch {

	private Long id;

	private User clientuseridUsers;
	
	private Artist artistidArtists;
	
	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	
}
