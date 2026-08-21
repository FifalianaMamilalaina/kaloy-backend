package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.Event;

import org.example.mozika.models.User;

import org.example.mozika.models.MediaType;



@Getter @Setter
public class EventMediaSearch {

	private Long id;

	private Event eventidEvents;
	
	private User uploaderuseridUsers;
	
	private MediaType mediatypeidMediaTypes;
	
	private String url;
	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	
}
