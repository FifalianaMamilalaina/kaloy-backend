package org.example.mozika.dto.playlist;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistUiItemResponse {

	private Long id;
	private String name;
	private String coverUrl;
	private int nombreTitres;
	private int dureeSecondes;
	private boolean estTelecharge;
	private String shareToken;
	private String updatedAt;
	private String visibility;
	private String ownerName;
	private String ownerPhotoUrl;
	private boolean owner;

	public PlaylistUiItemResponse(Long id, String name, String coverUrl,
			int nombreTitres, int dureeSecondes, boolean estTelecharge,
			String shareToken, String updatedAt, String visibility, String ownerName,
			String ownerPhotoUrl, boolean owner) {
		this.id = id;
		this.name = name;
		this.coverUrl = coverUrl;
		this.nombreTitres = nombreTitres;
		this.dureeSecondes = dureeSecondes;
		this.estTelecharge = estTelecharge;
		this.shareToken = shareToken;
		this.updatedAt = updatedAt;
		this.visibility = visibility;
		this.ownerName = ownerName;
		this.ownerPhotoUrl = ownerPhotoUrl;
		this.owner = owner;
	}
}
