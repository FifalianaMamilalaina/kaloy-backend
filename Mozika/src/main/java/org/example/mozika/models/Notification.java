package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="notifications")
public class Notification  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="user_id")
	@NotNull(message = "UserId cannot be null")
	private User useridUsers;
	@ManyToOne
	@JoinColumn(name="type_id")
	@NotNull(message = "TypeId cannot be null")
	private NotificationType typeidNotificationTypes;
	@Column(name="content", unique = false, nullable = false)
	@NotNull(message = "Content cannot be null")
	@Size(max = 2147483647)
	private String content;
	@ManyToOne
	@JoinColumn(name="related_entity_type_id")
	private InteractionTarget relatedentitytypeidInteractionTargets;
	@Column(name="related_entity_id", unique = false, nullable = true)
	private Long relatedEntityId;
	@Column(name="is_read", unique = false, nullable = false)
	@NotNull(message = "IsRead cannot be null")
	private Boolean isRead;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	

}
