package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="song_genres")
public class SongGenre  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="song_id")
	@NotNull(message = "SongId cannot be null")
	private Song songidSongs;
	@ManyToOne
	@JoinColumn(name="genre_id")
	@NotNull(message = "GenreId cannot be null")
	private Genre genreidGenres;
	

}
