package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="likes")
public class Like  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="user_id")
	@NotNull(message = "UserId cannot be null")
	private User useridUsers;
	@ManyToOne
	@JoinColumn(name="target_type_id")
	@NotNull(message = "TargetTypeId cannot be null")
	private InteractionTarget targettypeidInteractionTargets;
	@Column(name="target_id", unique = false, nullable = false)
	@NotNull(message = "TargetId cannot be null")
	private Long targetId;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	

}
