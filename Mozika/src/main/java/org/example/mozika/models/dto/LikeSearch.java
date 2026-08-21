package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.User;

import org.example.mozika.models.InteractionTarget;



@Getter @Setter
public class LikeSearch {

	private Long id;

	private User useridUsers;
	
	private InteractionTarget targettypeidInteractionTargets;
	
	private Long targetId;
	private Long targetIdMin;
	private Long targetIdMax;

	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	
}
