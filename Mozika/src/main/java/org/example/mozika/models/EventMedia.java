package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="event_media")
public class EventMedia  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="event_id")
	@NotNull(message = "EventId cannot be null")
	private Event eventidEvents;
	@ManyToOne
	@JoinColumn(name="uploader_user_id")
	@NotNull(message = "UploaderUserId cannot be null")
	private User uploaderuseridUsers;
	@ManyToOne
	@JoinColumn(name="media_type_id")
	@NotNull(message = "MediaTypeId cannot be null")
	private MediaType mediatypeidMediaTypes;
	@Column(name="url", unique = false, nullable = false)
	@NotNull(message = "Url cannot be null")
	@Size(max = 2147483647)
	private String url;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	

}
