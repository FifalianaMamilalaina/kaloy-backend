package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.User;

import org.example.mozika.models.PlaylistVisibilitie;



@Getter @Setter
public class PlaylistSearch {

	private Long id;

	private User owneruseridUsers;
	
	private String name;
	private PlaylistVisibilitie visibilityidPlaylistVisibilities;
	
	private String shareToken;
	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	private java.time.LocalDateTime updatedAt;
	private java.time.LocalDateTime updatedAtMin;
	private java.time.LocalDateTime updatedAtMax;

	
}
