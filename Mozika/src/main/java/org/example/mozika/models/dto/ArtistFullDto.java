package org.example.mozika.models.dto;

import org.example.mozika.models.Artist;

import org.example.mozika.models.Album;
import org.example.mozika.models.ArtistGroupMember;
import org.example.mozika.models.Concert;
import org.example.mozika.models.ContentSubmission;
import org.example.mozika.models.EditorialPlaylist;
import org.example.mozika.models.Event;
import org.example.mozika.models.Follow;
import org.example.mozika.models.Song;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArtistFullDto {
    private Artist artist;
    
    private List<Album> albums;
    private List<ArtistGroupMember> artistGroupMembers;
    private List<Concert> concerts;
    private List<ContentSubmission> contentSubmissions;
    private List<EditorialPlaylist> editorialPlaylists;
    private List<Event> events;
    private List<Follow> follows;
    private List<Song> songs;
    
}
