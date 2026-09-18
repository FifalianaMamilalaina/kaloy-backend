package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.Artist;

import org.example.mozika.models.Album;

import org.example.mozika.models.AudioStorageType;



@Getter @Setter
public class SongSearch {

	private Long id;

	private Artist artistidArtists;
	
	private Album albumidAlbums;
	
	private String title;
	private Integer durationSeconds;
	private Integer durationSecondsMin;
	private Integer durationSecondsMax;

	private java.time.LocalDate releaseDate;
	private java.time.LocalDate releaseDateMin;
	private java.time.LocalDate releaseDateMax;

	private String language;
	private String authorComposer;
	private String musicalArranger;
	private String recordingLocation;
	private java.time.LocalDate recordingDate;
	private java.time.LocalDate recordingDateMin;
	private java.time.LocalDate recordingDateMax;

	private AudioStorageType storagetypeidAudioStorageTypes;
	
	private String audioUrl;
	private String videoUrl;
	private String karaokeAudioUrl;
	private String lyrics;
	private String lyricsSyncData;
	private String solfaUrl;
	private String playbackUrl;
	private Boolean isDownloadable;
	private java.time.LocalDateTime createdAt;
	private java.time.LocalDateTime createdAtMin;
	private java.time.LocalDateTime createdAtMax;

	
}
