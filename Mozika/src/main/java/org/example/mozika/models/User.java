package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="users")
public class User  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@Column(name="email", unique = true, nullable = true)
	@Size(max = 255)
	private String email;
	@Column(name="phone", unique = true, nullable = true)
	@Size(max = 20)
	private String phone;
	@Column(name="email_verified_at", unique = false, nullable = true)
	private java.time.LocalDateTime emailVerifiedAt;
	@Column(name="phone_verified_at", unique = false, nullable = true)
	private java.time.LocalDateTime phoneVerifiedAt;
	@Column(name="password_hash", unique = false, nullable = false)
	@NotNull(message = "PasswordHash cannot be null")
	@Size(max = 255)
	private String passwordHash;
	@ManyToOne
	@JoinColumn(name="role_id")
	@NotNull(message = "RoleId cannot be null")
	private UserRole roleidUserRoles;
	@ManyToOne
	@JoinColumn(name="status_id")
	@NotNull(message = "StatusId cannot be null")
	private UserStatuse statusidUserStatuses;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	@Column(name="updated_at", unique = false, nullable = false)
	@NotNull(message = "UpdatedAt cannot be null")
	private java.time.LocalDateTime updatedAt;
	

}
