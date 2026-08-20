package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="artists")
public class Artist  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="user_id")
	@NotNull(message = "UserId cannot be null")
	private User useridUsers;
	@ManyToOne
	@JoinColumn(name="artist_type_id")
	@NotNull(message = "ArtistTypeId cannot be null")
	private ArtistType artisttypeidArtistTypes;
	@Column(name="stage_name", unique = false, nullable = false)
	@NotNull(message = "StageName cannot be null")
	@Size(max = 100)
	private String stageName;
	@Column(name="active_since_year", unique = false, nullable = true)
	private Integer activeSinceYear;
	@Column(name="photo_url", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String photoUrl;
	@Column(name="bio", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String bio;
	@ManyToOne
	@JoinColumn(name="verification_status_id")
	@NotNull(message = "VerificationStatusId cannot be null")
	private VerificationStatuse verificationstatusidVerificationStatuses;
	@Column(name="verified_at", unique = false, nullable = true)
	private java.time.LocalDateTime verifiedAt;
	@Column(name="is_certified", unique = false, nullable = false)
	@NotNull(message = "IsCertified cannot be null")
	private Boolean isCertified;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	

}
