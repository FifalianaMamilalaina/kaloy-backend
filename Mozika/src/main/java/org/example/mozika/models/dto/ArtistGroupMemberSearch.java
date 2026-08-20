package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.Artist;

import org.example.mozika.models.Artist;

import org.example.mozika.models.InstrumentRole;

import org.example.mozika.models.MemberStatuse;



@Getter @Setter
public class ArtistGroupMemberSearch {

	private Long id;

	private Artist groupartistidArtists;
	
	private Artist memberartistidArtists;
	
	private String fullName;
	private InstrumentRole roleinstrumentidInstrumentRoles;
	
	private String photoUrl;
	private MemberStatuse statusidMemberStatuses;
	
	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	
}
