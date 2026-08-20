package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.User;

import org.example.mozika.models.UserStatuse;

import org.example.mozika.models.UserStatuse;

import org.example.mozika.models.User;



@Getter @Setter
public class UserStatusHistorySearch {

	private Long id;

	private User useridUsers;
	
	private UserStatuse previousstatusidUserStatuses;
	
	private UserStatuse newstatusidUserStatuses;
	
	private String reason;
	private User changedbyuseridUsers;
	
	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	
}
