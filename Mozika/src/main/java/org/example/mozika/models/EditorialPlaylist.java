package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="editorial_playlists")
public class EditorialPlaylist  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="artist_id")
	@NotNull(message = "ArtistId cannot be null")
	private Artist artistidArtists;
	@Column(name="title", unique = false, nullable = false)
	@NotNull(message = "Title cannot be null")
	@Size(max = 150)
	private String title;
	@Column(name="description", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String description;
	@Column(name="cover_url", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String coverUrl;
	@Column(name="is_featured", unique = false, nullable = false)
	@NotNull(message = "IsFeatured cannot be null")
	private Boolean isFeatured;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	

}
