package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="reports")
public class Report  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="reporter_user_id")
	@NotNull(message = "ReporterUserId cannot be null")
	private User reporteruseridUsers;
	@ManyToOne
	@JoinColumn(name="target_type_id")
	@NotNull(message = "TargetTypeId cannot be null")
	private InteractionTarget targettypeidInteractionTargets;
	@Column(name="target_id", unique = false, nullable = false)
	@NotNull(message = "TargetId cannot be null")
	private Long targetId;
	@Column(name="reason", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String reason;
	@ManyToOne
	@JoinColumn(name="status_id")
	@NotNull(message = "StatusId cannot be null")
	private ReportStatuse statusidReportStatuses;
	@Column(name="reviewed_at", unique = false, nullable = true)
	private java.time.LocalDateTime reviewedAt;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	

}
