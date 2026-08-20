package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="editorial_playlist_songs")
public class EditorialPlaylistSong  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="editorial_playlist_id")
	@NotNull(message = "EditorialPlaylistId cannot be null")
	private EditorialPlaylist editorialplaylistidEditorialPlaylists;
	@ManyToOne
	@JoinColumn(name="song_id")
	@NotNull(message = "SongId cannot be null")
	private Song songidSongs;
	@Column(name="position", unique = false, nullable = false)
	@NotNull(message = "Position cannot be null")
	private Integer position;
	

}
