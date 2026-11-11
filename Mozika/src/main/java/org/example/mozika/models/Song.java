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
	// Pas de @NotNull : une chanson peut etre declaree avec seulement des
	// paroles ou un solfa, donc sans aucun fichier audio. La colonne a ete
	// rendue nullable en base le 30/10/2026 pour la meme raison.
	@ManyToOne
	@JoinColumn(name="storage_type_id")
	private AudioStorageType storagetypeidAudioStorageTypes;
	// nullable et sans @NotNull : meme raison que le type de stockage. Une
	// chanson declaree sur ses seules paroles n'a aucune URL audio a fournir.
	// La colonne est nullable en base depuis l'origine ; seule l'annotation
	// l'interdisait.
	@Column(name="audio_url", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String audioUrl;
	@Column(name="video_url", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String videoUrl;
	@Column(name="karaoke_audio_url", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String karaokeAudioUrl;
	// columnDefinition explicite : sans elle Hibernate a cree un
	// varchar(255) malgre le @Size ci-dessous, et les paroles reprises du
	// site font 1500 caracteres et plus. Une insertion trop longue est
	// refusee par PostgreSQL, elle n'est pas tronquee.
	@Column(name="lyrics", unique = false, nullable = true, columnDefinition = "TEXT")
	@Size(max = 2147483647)
	private String lyrics;
	@Column(name="lyrics_sync_data", unique = false, nullable = true)
	@org.hibernate.annotations.ColumnTransformer(read = "CAST(lyrics_sync_data as varchar)", write = "CAST(? as jsonb)")
	private String lyricsSyncData;
	@Column(name="solfa_url", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String solfaUrl;
	// La colonne solfa est en TEXT en base mais n'etait pas mappee : le
	// solfa saisi partait dans solfa_url, un varchar(255). Un solfa fait
	// plusieurs lignes, l'insertion aurait echoue au premier usage reel,
	// exactement comme les paroles (annexe § 1.15). solfa_url reste reservee
	// a un envoi de fichier, qui n'est pas encore fait.
	@Column(name="solfa", unique = false, nullable = true, columnDefinition = "TEXT")
	@Size(max = 2147483647)
	private String solfa;
	@Column(name="playback_url", unique = false, nullable = true)
	@Size(max = 2147483647)
	private String playbackUrl;
	@Column(name="is_downloadable", unique = false, nullable = false)
	@NotNull(message = "IsDownloadable cannot be null")
	private Boolean isDownloadable;
	@Column(name="created_at", unique = false, nullable = false)
	@NotNull(message = "CreatedAt cannot be null")
	private java.time.LocalDateTime createdAt;
	

}
