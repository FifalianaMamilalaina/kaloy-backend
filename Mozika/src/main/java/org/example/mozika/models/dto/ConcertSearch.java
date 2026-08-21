package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.Event;

import org.example.mozika.models.Artist;

import org.example.mozika.models.Venue;

import org.example.mozika.models.ParticipationStatuse;

import org.example.mozika.models.Artist;

import org.example.mozika.models.EventModerationStatuse;



@Getter @Setter
public class ConcertSearch {

	private Long id;

	private Event eventidEvents;
	
	private String title;
	private String description;
	private Artist artistidArtists;
	
	private Venue venueidVenues;
	
	private java.time.LocalDateTime startTime;
	private java.time.LocalDateTime startTimeMin;
	private java.time.LocalDateTime startTimeMax;

	private java.time.LocalDateTime endTime;
	private java.time.LocalDateTime endTimeMin;
	private java.time.LocalDateTime endTimeMax;

	private ParticipationStatuse statusidParticipationStatuses;
	
	private java.time.LocalDateTime respondedAt;
	private java.time.LocalDateTime respondedAtMin;
	private java.time.LocalDateTime respondedAtMax;

	private Artist createdbyartistidArtists;
	
	private EventModerationStatuse moderationstatusidEventModerationStatuses;
	
	private java.time.LocalDateTime reviewedAt;
	private java.time.LocalDateTime reviewedAtMin;
	private java.time.LocalDateTime reviewedAtMax;

	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	
}
