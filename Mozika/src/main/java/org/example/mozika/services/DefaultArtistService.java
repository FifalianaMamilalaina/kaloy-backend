package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.Artist;
import org.example.mozika.models.dto.ArtistSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.ArtistRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.ArtistService;
import org.example.mozika.specification.ArtistSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;

import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import org.example.mozika.repositories.AlbumRepository;
import org.example.mozika.models.Album;
import org.example.mozika.repositories.ArtistGroupMemberRepository;
import org.example.mozika.models.ArtistGroupMember;
import org.example.mozika.repositories.ConcertRepository;
import org.example.mozika.models.Concert;
import org.example.mozika.repositories.ContentSubmissionRepository;
import org.example.mozika.models.ContentSubmission;
import org.example.mozika.repositories.EditorialPlaylistRepository;
import org.example.mozika.models.EditorialPlaylist;
import org.example.mozika.repositories.EventRepository;
import org.example.mozika.models.Event;
import org.example.mozika.repositories.FollowRepository;
import org.example.mozika.models.Follow;
import org.example.mozika.repositories.SongRepository;
import org.example.mozika.models.Song;


@Service
public class DefaultArtistService implements ArtistService {
	private final ArtistRepository artistRepository;
private final AlbumRepository albumRepository;
private final ArtistGroupMemberRepository artistGroupMemberRepository;
private final ConcertRepository concertRepository;
private final ContentSubmissionRepository contentSubmissionRepository;
private final EditorialPlaylistRepository editorialPlaylistRepository;
private final EventRepository eventRepository;
private final FollowRepository followRepository;
private final SongRepository songRepository;


	public DefaultArtistService(ArtistRepository artistRepository, AlbumRepository albumRepository, ArtistGroupMemberRepository artistGroupMemberRepository, ConcertRepository concertRepository, ContentSubmissionRepository contentSubmissionRepository, EditorialPlaylistRepository editorialPlaylistRepository, EventRepository eventRepository, FollowRepository followRepository, SongRepository songRepository) {
	   this.artistRepository = artistRepository;
this.albumRepository = albumRepository;
this.artistGroupMemberRepository = artistGroupMemberRepository;
this.concertRepository = concertRepository;
this.contentSubmissionRepository = contentSubmissionRepository;
this.editorialPlaylistRepository = editorialPlaylistRepository;
this.eventRepository = eventRepository;
this.followRepository = followRepository;
this.songRepository = songRepository;

	}

	@Override
	public String exportArtistToCSV(List<Artist> artist) {
	   return ExportUtils.generateCsv(artist);
	}  

	@Override
	public Page<Artist> getAllArtist(Pageable pageable) {
	    try {
	        return artistRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving artist", ex);
	    }
	}

	@Override
	public Page<Artist> getAllArtist(Pageable pageable, ArtistSearch object) {
	    try {
	        Specification<Artist> spec=ArtistSpecification.filter(object);
	        return artistRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving artist", ex);
	    }
	}

	@Override
	public Artist getArtistById(Long id) {
	    Optional<Artist> artist = artistRepository.findById(id);
	    if (artist.isPresent()) {
	        return artist.get();
	    } else {
	        throw new ResourceNotFoundException("Artist not found with id : " + id);
	    }
	}

	@Override
	public Artist createArtist(Artist artist) {
	    try {
	        return artistRepository.save(artist);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating artist", ex);
	    }
	}

	@Override
	public Artist updateArtist(Long id, Artist artist) {
	    Optional<Artist> existingArtist = artistRepository.findById(id);
	    if (existingArtist.isPresent()) {
	        artist.setId(id);
	        try {
	            return artistRepository.save(artist);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating artist", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("Artist not found with id : " + id);
	    }
	}

	@Override
	public void deleteArtist(Long id) {
	    try {
	        artistRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting artist", ex);
	    }
	}


@Override
@Transactional
public Artist createFullArtist(Artist artist, List<Album> albums, List<ArtistGroupMember> artistGroupMembers, List<Concert> concerts, List<ContentSubmission> contentSubmissions, List<EditorialPlaylist> editorialPlaylists, List<Event> events, List<Follow> follows, List<Song> songs) {
  try {
    artist = artistRepository.save(artist);
    
    for (Album album : albums) {
      album.setArtistidArtists(artist);
      album.setId(null);
    }
    
    albumRepository.saveAll(albums);
    for (ArtistGroupMember artistGroupMember : artistGroupMembers) {
      artistGroupMember.setGroupartistidArtists(artist);
      artistGroupMember.setId(null);
    }
    
    artistGroupMemberRepository.saveAll(artistGroupMembers);
    for (Concert concert : concerts) {
      concert.setArtistidArtists(artist);
      concert.setId(null);
    }
    
    concertRepository.saveAll(concerts);
    for (ContentSubmission contentSubmission : contentSubmissions) {
      contentSubmission.setArtistidArtists(artist);
      contentSubmission.setId(null);
    }
    
    contentSubmissionRepository.saveAll(contentSubmissions);
    for (EditorialPlaylist editorialPlaylist : editorialPlaylists) {
      editorialPlaylist.setArtistidArtists(artist);
      editorialPlaylist.setId(null);
    }
    
    editorialPlaylistRepository.saveAll(editorialPlaylists);
    for (Event event : events) {
      event.setCreatedbyartistidArtists(artist);
      event.setId(null);
    }
    
    eventRepository.saveAll(events);
    for (Follow follow : follows) {
      follow.setArtistidArtists(artist);
      follow.setId(null);
    }
    
    followRepository.saveAll(follows);
    for (Song song : songs) {
      song.setArtistidArtists(artist);
      song.setId(null);
    }
    
    songRepository.saveAll(songs);
    
    
    return artist;
  } catch (Exception ex) {
    throw new InternalServerErrorException(
    "Error during atomic creation of Artist and its details.", ex);
  }
}
  
@Override
@Transactional
public Artist updateFullArtist(Long id, Artist artist, List<Album> albums, List<ArtistGroupMember> artistGroupMembers, List<Concert> concerts, List<ContentSubmission> contentSubmissions, List<EditorialPlaylist> editorialPlaylists, List<Event> events, List<Follow> follows, List<Song> songs) {
  try {
    if (!artistRepository.existsById(id)) {
      throw new ResourceNotFoundException("Artist not found with id: " + id);
    }

    artist.setId(id);
    artist = artistRepository.save(artist);

    List<Album> existingAlbums = albumRepository.findByArtistidArtists(artist);

    List<Album> albumsToDelete = existingAlbums.stream()
      .filter(existing -> albums.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!albumsToDelete.isEmpty()) {
      albumRepository.deleteAll(albumsToDelete);
    }

    for (Album album : albums) {
      album.setArtistidArtists(artist);
      
      if (album.getId() != null && 
          existingAlbums.stream()
            .anyMatch(e -> e.getId().equals(album.getId()))) {
          albumRepository.save(album);
      } else {
          album.setId(null);
          albumRepository.save(album);
      }
    }
    List<ArtistGroupMember> existingArtistGroupMembers = artistGroupMemberRepository.findByGroupartistidArtists(artist);

    List<ArtistGroupMember> artistGroupMembersToDelete = existingArtistGroupMembers.stream()
      .filter(existing -> artistGroupMembers.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!artistGroupMembersToDelete.isEmpty()) {
      artistGroupMemberRepository.deleteAll(artistGroupMembersToDelete);
    }

    for (ArtistGroupMember artistGroupMember : artistGroupMembers) {
      artistGroupMember.setGroupartistidArtists(artist);
      
      if (artistGroupMember.getId() != null && 
          existingArtistGroupMembers.stream()
            .anyMatch(e -> e.getId().equals(artistGroupMember.getId()))) {
          artistGroupMemberRepository.save(artistGroupMember);
      } else {
          artistGroupMember.setId(null);
          artistGroupMemberRepository.save(artistGroupMember);
      }
    }
    List<Concert> existingConcerts = concertRepository.findByArtistidArtists(artist);

    List<Concert> concertsToDelete = existingConcerts.stream()
      .filter(existing -> concerts.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!concertsToDelete.isEmpty()) {
      concertRepository.deleteAll(concertsToDelete);
    }

    for (Concert concert : concerts) {
      concert.setArtistidArtists(artist);
      
      if (concert.getId() != null && 
          existingConcerts.stream()
            .anyMatch(e -> e.getId().equals(concert.getId()))) {
          concertRepository.save(concert);
      } else {
          concert.setId(null);
          concertRepository.save(concert);
      }
    }
    List<ContentSubmission> existingContentSubmissions = contentSubmissionRepository.findByArtistidArtists(artist);

    List<ContentSubmission> contentSubmissionsToDelete = existingContentSubmissions.stream()
      .filter(existing -> contentSubmissions.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!contentSubmissionsToDelete.isEmpty()) {
      contentSubmissionRepository.deleteAll(contentSubmissionsToDelete);
    }

    for (ContentSubmission contentSubmission : contentSubmissions) {
      contentSubmission.setArtistidArtists(artist);
      
      if (contentSubmission.getId() != null && 
          existingContentSubmissions.stream()
            .anyMatch(e -> e.getId().equals(contentSubmission.getId()))) {
          contentSubmissionRepository.save(contentSubmission);
      } else {
          contentSubmission.setId(null);
          contentSubmissionRepository.save(contentSubmission);
      }
    }
    List<EditorialPlaylist> existingEditorialPlaylists = editorialPlaylistRepository.findByArtistidArtists(artist);

    List<EditorialPlaylist> editorialPlaylistsToDelete = existingEditorialPlaylists.stream()
      .filter(existing -> editorialPlaylists.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!editorialPlaylistsToDelete.isEmpty()) {
      editorialPlaylistRepository.deleteAll(editorialPlaylistsToDelete);
    }

    for (EditorialPlaylist editorialPlaylist : editorialPlaylists) {
      editorialPlaylist.setArtistidArtists(artist);
      
      if (editorialPlaylist.getId() != null && 
          existingEditorialPlaylists.stream()
            .anyMatch(e -> e.getId().equals(editorialPlaylist.getId()))) {
          editorialPlaylistRepository.save(editorialPlaylist);
      } else {
          editorialPlaylist.setId(null);
          editorialPlaylistRepository.save(editorialPlaylist);
      }
    }
    List<Event> existingEvents = eventRepository.findByCreatedbyartistidArtists(artist);

    List<Event> eventsToDelete = existingEvents.stream()
      .filter(existing -> events.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!eventsToDelete.isEmpty()) {
      eventRepository.deleteAll(eventsToDelete);
    }

    for (Event event : events) {
      event.setCreatedbyartistidArtists(artist);
      
      if (event.getId() != null && 
          existingEvents.stream()
            .anyMatch(e -> e.getId().equals(event.getId()))) {
          eventRepository.save(event);
      } else {
          event.setId(null);
          eventRepository.save(event);
      }
    }
    List<Follow> existingFollows = followRepository.findByArtistidArtists(artist);

    List<Follow> followsToDelete = existingFollows.stream()
      .filter(existing -> follows.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!followsToDelete.isEmpty()) {
      followRepository.deleteAll(followsToDelete);
    }

    for (Follow follow : follows) {
      follow.setArtistidArtists(artist);
      
      if (follow.getId() != null && 
          existingFollows.stream()
            .anyMatch(e -> e.getId().equals(follow.getId()))) {
          followRepository.save(follow);
      } else {
          follow.setId(null);
          followRepository.save(follow);
      }
    }
    List<Song> existingSongs = songRepository.findByArtistidArtists(artist);

    List<Song> songsToDelete = existingSongs.stream()
      .filter(existing -> songs.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!songsToDelete.isEmpty()) {
      songRepository.deleteAll(songsToDelete);
    }

    for (Song song : songs) {
      song.setArtistidArtists(artist);
      
      if (song.getId() != null && 
          existingSongs.stream()
            .anyMatch(e -> e.getId().equals(song.getId()))) {
          songRepository.save(song);
      } else {
          song.setId(null);
          songRepository.save(song);
      }
    }
    
    
    return artist;
  } catch (ResourceNotFoundException ex) {
    throw ex;
  } catch (Exception ex) {
    throw new InternalServerErrorException("Error during atomic update of Artist and its details.", ex);
  }
}
  
  @Override
  @Transactional
  public void deleteFullArtist(Long id) {
  try {
  Artist artist = artistRepository.findById(id)
  .orElseThrow(() -> new ResourceNotFoundException(
  "Artist not found with id: " + id));
        
        List<Album> albumsToDelete = albumRepository.findByArtistidArtists(artist);
        
        if (!albumsToDelete.isEmpty()) {
            albumRepository.deleteAll(albumsToDelete);
        }
        List<ArtistGroupMember> artistGroupMembersToDelete = artistGroupMemberRepository.findByGroupartistidArtists(artist);
        
        if (!artistGroupMembersToDelete.isEmpty()) {
            artistGroupMemberRepository.deleteAll(artistGroupMembersToDelete);
        }
        List<Concert> concertsToDelete = concertRepository.findByArtistidArtists(artist);
        
        if (!concertsToDelete.isEmpty()) {
            concertRepository.deleteAll(concertsToDelete);
        }
        List<ContentSubmission> contentSubmissionsToDelete = contentSubmissionRepository.findByArtistidArtists(artist);
        
        if (!contentSubmissionsToDelete.isEmpty()) {
            contentSubmissionRepository.deleteAll(contentSubmissionsToDelete);
        }
        List<EditorialPlaylist> editorialPlaylistsToDelete = editorialPlaylistRepository.findByArtistidArtists(artist);
        
        if (!editorialPlaylistsToDelete.isEmpty()) {
            editorialPlaylistRepository.deleteAll(editorialPlaylistsToDelete);
        }
        List<Event> eventsToDelete = eventRepository.findByCreatedbyartistidArtists(artist);
        
        if (!eventsToDelete.isEmpty()) {
            eventRepository.deleteAll(eventsToDelete);
        }
        List<Follow> followsToDelete = followRepository.findByArtistidArtists(artist);
        
        if (!followsToDelete.isEmpty()) {
            followRepository.deleteAll(followsToDelete);
        }
        List<Song> songsToDelete = songRepository.findByArtistidArtists(artist);
        
        if (!songsToDelete.isEmpty()) {
            songRepository.deleteAll(songsToDelete);
        }
        
        
        artistRepository.delete(artist);
        
    } catch (ResourceNotFoundException ex) {
        throw ex;
    } catch (Exception ex) {
        throw new InternalServerErrorException(
            "Error during atomic deletion of Artist and its details.", ex);
}
}
  

}
