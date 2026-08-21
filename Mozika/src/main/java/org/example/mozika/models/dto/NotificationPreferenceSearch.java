package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.User;

import org.example.mozika.models.NotificationType;



@Getter @Setter
public class NotificationPreferenceSearch {

	private Long id;

	private User useridUsers;
	
	private NotificationType notificationtypeidNotificationTypes;
	
	private Boolean isEnabled;
	
}
