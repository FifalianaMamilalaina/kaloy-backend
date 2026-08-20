package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.User;

import org.example.mozika.models.NotificationType;

import org.example.mozika.models.InteractionTarget;



@Getter @Setter
public class NotificationSearch {

	private Long id;

	private User useridUsers;
	
	private NotificationType typeidNotificationTypes;
	
	private String content;
	private InteractionTarget relatedentitytypeidInteractionTargets;
	
	private Long relatedEntityId;
	private Long relatedEntityIdMin;
	private Long relatedEntityIdMax;

	private Boolean isRead;
	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	
}
