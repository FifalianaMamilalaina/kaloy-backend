package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "playlist_generation_criteria")
public class PlaylistGenerationCriteria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "playlist_id")
	@NotNull(message = "PlaylistId cannot be null")
	private Playlist playlistidPlaylists;

	@Column(name = "target_duration_seconds", nullable = true)
	private Integer targetDurationSeconds;

	@Column(name = "created_at", nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
}
