package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="verification_codes")
public class VerificationCode  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="user_id")
	@NotNull(message = "UserId cannot be null")
	private User useridUsers;
	@ManyToOne
	@JoinColumn(name="channel_id")
	@NotNull(message = "ChannelId cannot be null")
	private VerificationChannel channelidVerificationChannels;
	@Column(name="destination", unique = false, nullable = false)
	@NotNull(message = "Destination cannot be null")
	@Size(max = 255)
	private String destination;
	@Column(name="code", unique = false, nullable = false)
	@NotNull(message = "Code cannot be null")
	@Size(max = 10)
	private String code;
	@Column(name="expires_at", unique = false, nullable = false)
	@NotNull(message = "ExpiresAt cannot be null")
	private java.time.LocalDateTime expiresAt;
	@Column(name="consumed_at", unique = false, nullable = true)
	private java.time.LocalDateTime consumedAt;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	

}
