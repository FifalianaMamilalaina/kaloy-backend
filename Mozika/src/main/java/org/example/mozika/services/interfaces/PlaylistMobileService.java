package org.example.mozika.services.interfaces;

import org.example.mozika.dto.playlist.CreatePlaylistMobileRequest;
import org.example.mozika.dto.playlist.MergePlaylistsMobileRequest;
import org.example.mozika.dto.playlist.PlaylistUiItemResponse;
import org.example.mozika.models.EditorialPlaylist;
import org.example.mozika.models.Playlist;

import java.util.List;

public interface PlaylistMobileService {

	List<PlaylistUiItemResponse> getMesPlaylists(String email);

	List<EditorialPlaylist> getFeaturedEditorials();

	Playlist createPlaylist(String email, CreatePlaylistMobileRequest request);

	void deletePlaylist(String email, Long playlistId);

	Playlist mergePlaylist(String email, MergePlaylistsMobileRequest request);

	void downloadPlaylist(String email, Long playlistId);
}
