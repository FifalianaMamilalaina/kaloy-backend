package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="events")
public class Event  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@Column(name="name", unique = false, nullable = false)
	@NotNull(message = "Name cannot be null")
	@Size(max = 150)
	private String name;
	@Column(name="description", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String description;
	@Column(name="start_date", unique = false, nullable = false)
	@NotNull(message = "StartDate cannot be null")
	private java.time.LocalDate startDate;
	@Column(name="end_date", unique = false, nullable = false)
	@NotNull(message = "EndDate cannot be null")
	private java.time.LocalDate endDate;
	@ManyToOne
	@JoinColumn(name="created_by_artist_id")
	@NotNull(message = "CreatedByArtistId cannot be null")
	private Artist createdbyartistidArtists;
	@ManyToOne
	@JoinColumn(name="moderation_status_id")
	@NotNull(message = "ModerationStatusId cannot be null")
	private EventModerationStatuse moderationstatusidEventModerationStatuses;
	@Column(name="reviewed_at", unique = false, nullable = true)
	private java.time.LocalDateTime reviewedAt;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	

}
