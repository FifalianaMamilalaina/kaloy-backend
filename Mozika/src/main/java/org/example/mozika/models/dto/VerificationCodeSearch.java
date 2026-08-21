package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.User;

import org.example.mozika.models.VerificationChannel;



@Getter @Setter
public class VerificationCodeSearch {

	private Long id;

	private User useridUsers;
	
	private VerificationChannel channelidVerificationChannels;
	
	private String destination;
	private String code;
	private java.time.LocalDateTime expiresAt;
	private java.time.LocalDateTime expiresAtMin;
	private java.time.LocalDateTime expiresAtMax;

	private java.time.LocalDateTime consumedAt;
	private java.time.LocalDateTime consumedAtMin;
	private java.time.LocalDateTime consumedAtMax;

	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	
}
