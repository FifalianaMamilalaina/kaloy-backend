package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="up_next_queue")
public class UpNextQueue  {
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
	@Column(name="position", unique = false, nullable = false)
	@NotNull(message = "Position cannot be null")
	private Integer position;
	@Column(name="added_at", unique = false, nullable = false)
	@NotNull(message = "AddedAt cannot be null")
	private java.time.LocalDateTime addedAt;
	

}
