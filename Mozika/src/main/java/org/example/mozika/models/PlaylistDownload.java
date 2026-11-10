package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "playlist_downloads")
public class PlaylistDownload {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@NotNull(message = "UserId cannot be null")
	private User useridUsers;

	@ManyToOne
	@JoinColumn(name = "playlist_id")
	@NotNull(message = "PlaylistId cannot be null")
	private Playlist playlistidPlaylists;

	@Column(name = "downloaded_at", nullable = false)
	@NotNull(message = "DownloadedAt cannot be null")
	private java.time.LocalDateTime downloadedAt;

	@Column(name = "expires_at", nullable = false)
	@NotNull(message = "ExpiresAt cannot be null")
	private java.time.LocalDateTime expiresAt;

	@Column(name = "is_active", nullable = false)
	@NotNull(message = "IsActive cannot be null")
	private Boolean isActive;
}
