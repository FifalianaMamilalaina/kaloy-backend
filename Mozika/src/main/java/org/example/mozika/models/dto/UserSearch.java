package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.UserRole;

import org.example.mozika.models.UserStatuse;



@Getter @Setter
public class UserSearch {

	private Long id;

	private String email;
	private String phone;
	private java.time.LocalDateTime emailVerifiedAt;
	private java.time.LocalDateTime emailVerifiedAtMin;
	private java.time.LocalDateTime emailVerifiedAtMax;

	private java.time.LocalDateTime phoneVerifiedAt;
	private java.time.LocalDateTime phoneVerifiedAtMin;
	private java.time.LocalDateTime phoneVerifiedAtMax;

	private String passwordHash;
	private UserRole roleidUserRoles;
	
	private UserStatuse statusidUserStatuses;
	
	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	private java.time.LocalDateTime updatedAt;
	private java.time.LocalDateTime updatedAtMin;
	private java.time.LocalDateTime updatedAtMax;

	
}
