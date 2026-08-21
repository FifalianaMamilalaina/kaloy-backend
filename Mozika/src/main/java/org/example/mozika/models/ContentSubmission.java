package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="content_submissions")
public class ContentSubmission  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="artist_id")
	@NotNull(message = "ArtistId cannot be null")
	private Artist artistidArtists;
	@Column(name="source_filename", unique = false, nullable = false)
	@NotNull(message = "SourceFilename cannot be null")
	@Size(max = 255)
	private String sourceFilename;
	@Column(name="file_type", unique = false, nullable = false)
	@NotNull(message = "FileType cannot be null")
	@Size(max = 20)
	private String fileType;
	@ManyToOne
	@JoinColumn(name="status_id")
	@NotNull(message = "StatusId cannot be null")
	private SubmissionStatuse statusidSubmissionStatuses;
	@Column(name="error_message", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String errorMessage;
	@Column(name="submitted_at", unique = false, nullable = false)
	@NotNull(message = "SubmittedAt cannot be null")
	private java.time.LocalDateTime submittedAt;
	@Column(name="processed_at", unique = false, nullable = true)
	private java.time.LocalDateTime processedAt;
	

}
