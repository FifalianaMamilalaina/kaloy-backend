package org.example.mozika.services.interfaces;

import org.example.mozika.models.Artist;
import org.example.mozika.models.Album;
import org.example.mozika.models.ArtistGroupMember;
import org.example.mozika.models.Concert;
import org.example.mozika.models.ContentSubmission;
import org.example.mozika.models.EditorialPlaylist;
import org.example.mozika.models.Event;
import org.example.mozika.models.Follow;
import org.example.mozika.models.Song;

import org.example.mozika.models.dto.ArtistSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface ArtistService {
    Page<Artist> getAllArtist(Pageable pageable);

    Page<Artist> getAllArtist(Pageable pageable, ArtistSearch object);

    Artist getArtistById(Long id);

    public String exportArtistToCSV(List<Artist> artist);

    

    Artist createArtist(Artist artist);

    Artist updateArtist(Long id, Artist artist);

    void deleteArtist(Long id);
    

    Artist createFullArtist(Artist artist,List<Album> albums,List<ArtistGroupMember> artistGroupMembers,List<Concert> concerts,List<ContentSubmission> contentSubmissions,List<EditorialPlaylist> editorialPlaylists,List<Event> events,List<Follow> follows,List<Song> songs);
    Artist updateFullArtist(Long id, Artist artist,List<Album> albums,List<ArtistGroupMember> artistGroupMembers,List<Concert> concerts,List<ContentSubmission> contentSubmissions,List<EditorialPlaylist> editorialPlaylists,List<Event> events,List<Follow> follows,List<Song> songs);
    void deleteFullArtist(Long id);
}
