package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "playlist_fusions")
public class PlaylistFusion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "result_playlist_id")
	@NotNull(message = "ResultPlaylistId cannot be null")
	private Playlist resultPlaylist;

	@ManyToOne
	@JoinColumn(name = "source_playlist_id")
	@NotNull(message = "SourcePlaylistId cannot be null")
	private Playlist sourcePlaylist;

	@Column(name = "fused_at", nullable = false)
	@NotNull(message = "FusedAt cannot be null")
	private java.time.LocalDateTime fusedAt;
}
