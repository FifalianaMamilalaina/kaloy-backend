package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.Artist;

import org.example.mozika.models.EventModerationStatuse;



@Getter @Setter
public class EventSearch {

	private Long id;

	private String name;
	private String description;
	private java.time.LocalDate startDate;
	private java.time.LocalDate startDateMin;
	private java.time.LocalDate startDateMax;

	private java.time.LocalDate endDate;
	private java.time.LocalDate endDateMin;
	private java.time.LocalDate endDateMax;

	private Artist createdbyartistidArtists;
	
	private EventModerationStatuse moderationstatusidEventModerationStatuses;
	
	private java.time.LocalDateTime reviewedAt;
	private java.time.LocalDateTime reviewedAtMin;
	private java.time.LocalDateTime reviewedAtMax;

	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	
}
