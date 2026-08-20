package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="comments")
public class Comment  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="author_user_id")
	@NotNull(message = "AuthorUserId cannot be null")
	private User authoruseridUsers;
	@ManyToOne
	@JoinColumn(name="target_type_id")
	@NotNull(message = "TargetTypeId cannot be null")
	private InteractionTarget targettypeidInteractionTargets;
	@Column(name="target_id", unique = false, nullable = false)
	@NotNull(message = "TargetId cannot be null")
	private Long targetId;
	@Column(name="content", unique = false, nullable = false)
	@NotNull(message = "Content cannot be null")
	@Size(max = 2147483647)
	private String content;
	@Column(name="is_hidden", unique = false, nullable = false)
	@NotNull(message = "IsHidden cannot be null")
	private Boolean isHidden;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	

}
