package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="listening_history")
public class ListeningHistory  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="user_id")
	@NotNull(message = "UserId cannot be null")
	private User useridUsers;
	@ManyToOne
	@JoinColumn(name="song_id")
	@NotNull(message = "SongId cannot be null")
	private Song songidSongs;
	@ManyToOne
	@JoinColumn(name="play_mode_id")
	@NotNull(message = "PlayModeId cannot be null")
	private PlayMode playmodeidPlayModes;
	@Column(name="listened_at", unique = false, nullable = false)
	@NotNull(message = "ListenedAt cannot be null")
	private java.time.LocalDateTime listenedAt;
	

}
