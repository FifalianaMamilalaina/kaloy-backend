package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="concerts")
public class Concert  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="event_id")
	private Event eventidEvents;
	@Column(name="title", unique = false, nullable = true)
	@Size(max = 150)
	private String title;
	@Column(name="description", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String description;
	@ManyToOne
	@JoinColumn(name="artist_id")
	@NotNull(message = "ArtistId cannot be null")
	private Artist artistidArtists;
	@ManyToOne
	@JoinColumn(name="venue_id")
	private Venue venueidVenues;
	@Column(name="start_time", unique = false, nullable = false)
	@NotNull(message = "StartTime cannot be null")
	private java.time.LocalDateTime startTime;
	@Column(name="end_time", unique = false, nullable = true)
	private java.time.LocalDateTime endTime;
	@ManyToOne
	@JoinColumn(name="status_id")
	@NotNull(message = "StatusId cannot be null")
	private ParticipationStatuse statusidParticipationStatuses;
	@Column(name="responded_at", unique = false, nullable = true)
	private java.time.LocalDateTime respondedAt;
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
