package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="follows")
public class Follow  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="client_user_id")
	@NotNull(message = "ClientUserId cannot be null")
	private User clientuseridUsers;
	@ManyToOne
	@JoinColumn(name="artist_id")
	@NotNull(message = "ArtistId cannot be null")
	private Artist artistidArtists;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	

}
