package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="songs")
public class Song  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	@ManyToOne
	@JoinColumn(name="artist_id")
	@NotNull(message = "ArtistId cannot be null")
	private Artist artistidArtists;
	@ManyToOne
	@JoinColumn(name="album_id")
	private Album albumidAlbums;
	@Column(name="title", unique = false, nullable = false)
	@NotNull(message = "Title cannot be null")
	@Size(max = 150)
	private String title;
	@Column(name="duration_seconds", unique = false, nullable = true)
	@DecimalMin(value = "0")
	private Integer durationSeconds;
	@Column(name="release_date", unique = false, nullable = true)
	private java.time.LocalDate releaseDate;
	@Column(name="language", unique = false, nullable = false)
	@NotNull(message = "Language cannot be null")
	@Size(max = 10)
	private String language;
	@Column(name="author_composer", unique = false, nullable = true)
	@Size(max = 255)
	private String authorComposer;
	@Column(name="musical_arranger", unique = false, nullable = true)
	@Size(max = 255)
	private String musicalArranger;
	@Column(name="recording_location", unique = false, nullable = true)
	@Size(max = 255)
	private String recordingLocation;
	@Column(name="recording_date", unique = false, nullable = true)
	private java.time.LocalDate recordingDate;
	@ManyToOne
	@JoinColumn(name="storage_type_id")
	@NotNull(message = "StorageTypeId cannot be null")
	private AudioStorageType storagetypeidAudioStorageTypes;
	@Column(name="audio_url", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String audioUrl;
	@Column(name="audio_file", unique = false, nullable = true)
	private byte[] audioFile;
	@Column(name="video_url", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String videoUrl;
	@Column(name="karaoke_audio", unique = false, nullable = true)
	private byte[] karaokeAudio;
	@Column(name="lyrics", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String lyrics;
	@Column(name="lyrics_sync_data", unique = false, nullable = true)
	@org.hibernate.annotations.ColumnTransformer(read = "CAST(lyrics_sync_data as varchar)", write = "CAST(? as jsonb)")
	private String lyricsSyncData;
	@Column(name="solfa", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String solfa;
	@Column(name="playback", unique = false, nullable = true)
	private byte[] playback;
	@Column(name="is_downloadable", unique = false, nullable = false)
	@NotNull(message = "IsDownloadable cannot be null")
	private Boolean isDownloadable;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	

}
