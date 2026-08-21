package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="user_status_history")
public class UserStatusHistory  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="user_id")
	@NotNull(message = "UserId cannot be null")
	private User useridUsers;
	@ManyToOne
	@JoinColumn(name="previous_status_id")
	private UserStatuse previousstatusidUserStatuses;
	@ManyToOne
	@JoinColumn(name="new_status_id")
	@NotNull(message = "NewStatusId cannot be null")
	private UserStatuse newstatusidUserStatuses;
	@Column(name="reason", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String reason;
	@ManyToOne
	@JoinColumn(name="changed_by_user_id")
	private User changedbyuseridUsers;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	

}
