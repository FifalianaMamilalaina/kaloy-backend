package org.example.mozika.services.interfaces;

import org.example.mozika.dto.playlist.AddSongsResult;
import org.example.mozika.dto.playlist.AddSongsToPlaylistRequest;
import org.example.mozika.dto.playlist.CreatePlaylistMobileRequest;
import org.example.mozika.dto.playlist.MergePlaylistsMobileRequest;
import org.example.mozika.dto.playlist.PlaylistSongMobileResponse;
import org.example.mozika.dto.playlist.PlaylistUiItemResponse;
import org.example.mozika.dto.playlist.PublicPlaylistPageResponse;
import org.example.mozika.dto.playlist.ReorderSongItem;
import org.example.mozika.dto.playlist.UpdatePlaylistMobileRequest;
import org.example.mozika.models.EditorialPlaylist;
import org.example.mozika.models.Playlist;

import java.util.List;

public interface PlaylistMobileService {

	List<PlaylistUiItemResponse> getMesPlaylists(String email);

	PlaylistUiItemResponse getPlaylist(String email, Long playlistId);

	PlaylistUiItemResponse changeVisibility(String email, Long playlistId, String visibility);

	PublicPlaylistPageResponse discoverPublicPlaylists(String email, String search, int page, int size);

	PlaylistUiItemResponse getPublicPlaylist(String email, Long playlistId);

	List<PlaylistSongMobileResponse> getPublicPlaylistSongs(String email, Long playlistId);

	List<EditorialPlaylist> getFeaturedEditorials();

	Playlist createPlaylist(String email, CreatePlaylistMobileRequest request);

	Playlist updatePlaylist(String email, Long playlistId, UpdatePlaylistMobileRequest request);

	void deletePlaylist(String email, Long playlistId);

	Playlist mergePlaylist(String email, MergePlaylistsMobileRequest request);

	void downloadPlaylist(String email, Long playlistId);

	List<PlaylistSongMobileResponse> getSongsInPlaylist(String email, Long playlistId);

	AddSongsResult addSongsToPlaylist(String email, Long playlistId, AddSongsToPlaylistRequest request);

	void removeSongFromPlaylist(String email, Long playlistId, Long songId);

	void reorderSongs(String email, Long playlistId, List<ReorderSongItem> items);
}
