package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="playlists")
public class Playlist  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="owner_user_id")
	@NotNull(message = "OwnerUserId cannot be null")
	private User owneruseridUsers;
	@Column(name="name", unique = false, nullable = false)
	@NotNull(message = "Name cannot be null")
	@Size(max = 150)
	private String name;
	@ManyToOne
	@JoinColumn(name="visibility_id")
	@NotNull(message = "VisibilityId cannot be null")
	private PlaylistVisibilitie visibilityidPlaylistVisibilities;
	@Column(name="share_token", unique = false, nullable = true)
	@Size(max = 64)
	private String shareToken;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	@Column(name="updated_at", unique = false, nullable = false)
	@NotNull(message = "UpdatedAt cannot be null")
	private java.time.LocalDateTime updatedAt;
	

}
