package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="artist_group_members")
public class ArtistGroupMember  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="group_artist_id")
	@NotNull(message = "GroupArtistId cannot be null")
	private Artist groupartistidArtists;
	@ManyToOne
	@JoinColumn(name="member_artist_id")
	private Artist memberartistidArtists;
	@Column(name="full_name", unique = false, nullable = true)
	@Size(max = 100)
	private String fullName;
	@ManyToOne
	@JoinColumn(name="role_instrument_id")
	private InstrumentRole roleinstrumentidInstrumentRoles;
	@Column(name="photo_url", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String photoUrl;
	@ManyToOne
	@JoinColumn(name="status_id")
	@NotNull(message = "StatusId cannot be null")
	private MemberStatuse statusidMemberStatuses;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	

}
