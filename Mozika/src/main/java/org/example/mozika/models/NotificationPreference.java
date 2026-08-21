package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="notification_preferences")
public class NotificationPreference  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="user_id")
	@NotNull(message = "UserId cannot be null")
	private User useridUsers;
	@ManyToOne
	@JoinColumn(name="notification_type_id")
	@NotNull(message = "NotificationTypeId cannot be null")
	private NotificationType notificationtypeidNotificationTypes;
	@Column(name="is_enabled", unique = false, nullable = false)
	@NotNull(message = "IsEnabled cannot be null")
	private Boolean isEnabled;
	

}
