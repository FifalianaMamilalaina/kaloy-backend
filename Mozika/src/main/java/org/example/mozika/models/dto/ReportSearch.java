package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.User;

import org.example.mozika.models.InteractionTarget;

import org.example.mozika.models.ReportStatuse;



@Getter @Setter
public class ReportSearch {

	private Long id;

	private User reporteruseridUsers;
	
	private InteractionTarget targettypeidInteractionTargets;
	
	private Long targetId;
	private Long targetIdMin;
	private Long targetIdMax;

	private String reason;
	private ReportStatuse statusidReportStatuses;
	
	private java.time.LocalDateTime reviewedAt;
	private java.time.LocalDateTime reviewedAtMin;
	private java.time.LocalDateTime reviewedAtMax;

	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	
}
