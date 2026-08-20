package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.User;

import org.example.mozika.models.InteractionTarget;



@Getter @Setter
public class CommentSearch {

	private Long id;

	private User authoruseridUsers;
	
	private InteractionTarget targettypeidInteractionTargets;
	
	private Long targetId;
	private Long targetIdMin;
	private Long targetIdMax;

	private String content;
	private Boolean isHidden;
	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	
}
