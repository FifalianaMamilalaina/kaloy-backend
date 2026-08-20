package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.User;

import org.example.mozika.models.ArtistType;

import org.example.mozika.models.VerificationStatuse;



@Getter @Setter
public class ArtistSearch {

	private Long id;

	private User useridUsers;
	
	private ArtistType artisttypeidArtistTypes;
	
	private String stageName;
	private Integer activeSinceYear;
	private Integer activeSinceYearMin;
	private Integer activeSinceYearMax;

	private String photoUrl;
	private String bio;
	private VerificationStatuse verificationstatusidVerificationStatuses;
	
	private java.time.LocalDateTime verifiedAt;
	private java.time.LocalDateTime verifiedAtMin;
	private java.time.LocalDateTime verifiedAtMax;

	private Boolean isCertified;
	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	
}
