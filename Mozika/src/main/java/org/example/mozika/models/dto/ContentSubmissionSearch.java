package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.Artist;

import org.example.mozika.models.SubmissionStatuse;



@Getter @Setter
public class ContentSubmissionSearch {

	private Long id;

	private Artist artistidArtists;
	
	private String sourceFilename;
	private String fileType;
	private SubmissionStatuse statusidSubmissionStatuses;
	
	private String errorMessage;
	private java.time.LocalDateTime submittedAt;
	private java.time.LocalDateTime submittedAtMin;
	private java.time.LocalDateTime submittedAtMax;

	private java.time.LocalDateTime processedAt;
	private java.time.LocalDateTime processedAtMin;
	private java.time.LocalDateTime processedAtMax;

	
}
