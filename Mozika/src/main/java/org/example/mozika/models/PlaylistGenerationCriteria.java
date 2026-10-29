package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

	@JdbcTypeCode(SqlTypes.ARRAY)
	@Column(name = "included_genres", columnDefinition = "text[]")
	private String[] includedGenres;

	@JdbcTypeCode(SqlTypes.ARRAY)
	@Column(name = "excluded_genres", columnDefinition = "text[]")
	private String[] excludedGenres;

	@JdbcTypeCode(SqlTypes.ARRAY)
	@Column(name = "included_artist_ids", columnDefinition = "bigint[]")
	private Long[] includedArtistIds;

	@JdbcTypeCode(SqlTypes.ARRAY)
	@Column(name = "excluded_artist_ids", columnDefinition = "bigint[]")
	private Long[] excludedArtistIds;

	@Column(name = "created_at", nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
}
