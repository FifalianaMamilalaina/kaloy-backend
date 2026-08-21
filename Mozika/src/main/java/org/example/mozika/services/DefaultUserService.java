package org.example.mozika.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;      
import org.springframework.data.jpa.domain.Specification;
import org.example.mozika.models.User;
import org.example.mozika.models.dto.UserSearch;
import org.springframework.stereotype.Service;
import org.example.mozika.repositories.UserRepository;
import java.util.Optional;
import org.example.mozika.services.interfaces.UserService;
import org.example.mozika.specification.UserSpecification;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.utils.ExportUtils;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;

import jakarta.transaction.Transactional;
import java.util.stream.Collectors;
import org.example.mozika.repositories.ArtistRepository;
import org.example.mozika.models.Artist;
import org.example.mozika.repositories.ClientRepository;
import org.example.mozika.models.Client;
import org.example.mozika.repositories.CommentRepository;
import org.example.mozika.models.Comment;
import org.example.mozika.repositories.DownloadRepository;
import org.example.mozika.models.Download;
import org.example.mozika.repositories.EventMediaRepository;
import org.example.mozika.models.EventMedia;
import org.example.mozika.repositories.FollowRepository;
import org.example.mozika.models.Follow;
import org.example.mozika.repositories.LikeRepository;
import org.example.mozika.models.Like;
import org.example.mozika.repositories.ListeningHistoryRepository;
import org.example.mozika.models.ListeningHistory;
import org.example.mozika.repositories.NotificationPreferenceRepository;
import org.example.mozika.models.NotificationPreference;
import org.example.mozika.repositories.NotificationRepository;
import org.example.mozika.models.Notification;
import org.example.mozika.repositories.PlaylistRepository;
import org.example.mozika.models.Playlist;
import org.example.mozika.repositories.ReportRepository;
import org.example.mozika.models.Report;
import org.example.mozika.repositories.SearchHistoryRepository;
import org.example.mozika.models.SearchHistory;
import org.example.mozika.repositories.UpNextQueueRepository;
import org.example.mozika.models.UpNextQueue;
import org.example.mozika.repositories.UserStatusHistoryRepository;
import org.example.mozika.models.UserStatusHistory;
import org.example.mozika.repositories.VerificationCodeRepository;
import org.example.mozika.models.VerificationCode;


@Service
public class DefaultUserService implements UserService {
	private final UserRepository userRepository;
private final ArtistRepository artistRepository;
private final ClientRepository clientRepository;
private final CommentRepository commentRepository;
private final DownloadRepository downloadRepository;
private final EventMediaRepository eventMediaRepository;
private final FollowRepository followRepository;
private final LikeRepository likeRepository;
private final ListeningHistoryRepository listeningHistoryRepository;
private final NotificationPreferenceRepository notificationPreferenceRepository;
private final NotificationRepository notificationRepository;
private final PlaylistRepository playlistRepository;
private final ReportRepository reportRepository;
private final SearchHistoryRepository searchHistoryRepository;
private final UpNextQueueRepository upNextQueueRepository;
private final UserStatusHistoryRepository userStatusHistoryRepository;
private final VerificationCodeRepository verificationCodeRepository;


	public DefaultUserService(UserRepository userRepository, ArtistRepository artistRepository, ClientRepository clientRepository, CommentRepository commentRepository, DownloadRepository downloadRepository, EventMediaRepository eventMediaRepository, FollowRepository followRepository, LikeRepository likeRepository, ListeningHistoryRepository listeningHistoryRepository, NotificationPreferenceRepository notificationPreferenceRepository, NotificationRepository notificationRepository, PlaylistRepository playlistRepository, ReportRepository reportRepository, SearchHistoryRepository searchHistoryRepository, UpNextQueueRepository upNextQueueRepository, UserStatusHistoryRepository userStatusHistoryRepository, VerificationCodeRepository verificationCodeRepository) {
	   this.userRepository = userRepository;
this.artistRepository = artistRepository;
this.clientRepository = clientRepository;
this.commentRepository = commentRepository;
this.downloadRepository = downloadRepository;
this.eventMediaRepository = eventMediaRepository;
this.followRepository = followRepository;
this.likeRepository = likeRepository;
this.listeningHistoryRepository = listeningHistoryRepository;
this.notificationPreferenceRepository = notificationPreferenceRepository;
this.notificationRepository = notificationRepository;
this.playlistRepository = playlistRepository;
this.reportRepository = reportRepository;
this.searchHistoryRepository = searchHistoryRepository;
this.upNextQueueRepository = upNextQueueRepository;
this.userStatusHistoryRepository = userStatusHistoryRepository;
this.verificationCodeRepository = verificationCodeRepository;

	}

	@Override
	public String exportUserToCSV(List<User> user) {
	   return ExportUtils.generateCsv(user);
	}  

	@Override
	public Page<User> getAllUser(Pageable pageable) {
	    try {
	        return userRepository.findAll(pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving user", ex);
	    }
	}

	@Override
	public Page<User> getAllUser(Pageable pageable, UserSearch object) {
	    try {
	        Specification<User> spec=UserSpecification.filter(object);
	        return userRepository.findAll(spec, pageable);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while retrieving user", ex);
	    }
	}

	@Override
	public User getUserById(Long id) {
	    Optional<User> user = userRepository.findById(id);
	    if (user.isPresent()) {
	        return user.get();
	    } else {
	        throw new ResourceNotFoundException("User not found with id : " + id);
	    }
	}

	@Override
	public User createUser(User user) {
	    try {
	        return userRepository.save(user);
	    } catch (DataIntegrityViolationException ex) {
	        throw ex;
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while creating user", ex);
	    }
	}

	@Override
	public User updateUser(Long id, User user) {
	    Optional<User> existingUser = userRepository.findById(id);
	    if (existingUser.isPresent()) {
	        user.setId(id);
	        try {
	            return userRepository.save(user);
	    } catch (DataIntegrityViolationException ex) {
	            throw ex;    
	    } catch (Exception ex) {
	            throw new InternalServerErrorException("Error while updating user", ex);
	        }
	    } else {
	        throw new ResourceNotFoundException("User not found with id : " + id);
	    }
	}

	@Override
	public void deleteUser(Long id) {
	    try {
	        userRepository.deleteById(id);
	    } catch (Exception ex) {
	        throw new InternalServerErrorException("Error while deleting user", ex);
	    }
	}


@Override
@Transactional
public User createFullUser(User user, List<Artist> artists, List<Client> clients, List<Comment> comments, List<Download> downloads, List<EventMedia> eventMedias, List<Follow> follows, List<Like> likes, List<ListeningHistory> listeningHistorys, List<NotificationPreference> notificationPreferences, List<Notification> notifications, List<Playlist> playlists, List<Report> reports, List<SearchHistory> searchHistorys, List<UpNextQueue> upNextQueues, List<UserStatusHistory> userStatusHistorys, List<VerificationCode> verificationCodes) {
  try {
    user = userRepository.save(user);
    
    for (Artist artist : artists) {
      artist.setUseridUsers(user);
      artist.setId(null);
    }
    
    artistRepository.saveAll(artists);
    for (Client client : clients) {
      client.setUseridUsers(user);
      client.setId(null);
    }
    
    clientRepository.saveAll(clients);
    for (Comment comment : comments) {
      comment.setAuthoruseridUsers(user);
      comment.setId(null);
    }
    
    commentRepository.saveAll(comments);
    for (Download download : downloads) {
      download.setUseridUsers(user);
      download.setId(null);
    }
    
    downloadRepository.saveAll(downloads);
    for (EventMedia eventMedia : eventMedias) {
      eventMedia.setUploaderuseridUsers(user);
      eventMedia.setId(null);
    }
    
    eventMediaRepository.saveAll(eventMedias);
    for (Follow follow : follows) {
      follow.setClientuseridUsers(user);
      follow.setId(null);
    }
    
    followRepository.saveAll(follows);
    for (Like like : likes) {
      like.setUseridUsers(user);
      like.setId(null);
    }
    
    likeRepository.saveAll(likes);
    for (ListeningHistory listeningHistory : listeningHistorys) {
      listeningHistory.setUseridUsers(user);
      listeningHistory.setId(null);
    }
    
    listeningHistoryRepository.saveAll(listeningHistorys);
    for (NotificationPreference notificationPreference : notificationPreferences) {
      notificationPreference.setUseridUsers(user);
      notificationPreference.setId(null);
    }
    
    notificationPreferenceRepository.saveAll(notificationPreferences);
    for (Notification notification : notifications) {
      notification.setUseridUsers(user);
      notification.setId(null);
    }
    
    notificationRepository.saveAll(notifications);
    for (Playlist playlist : playlists) {
      playlist.setOwneruseridUsers(user);
      playlist.setId(null);
    }
    
    playlistRepository.saveAll(playlists);
    for (Report report : reports) {
      report.setReporteruseridUsers(user);
      report.setId(null);
    }
    
    reportRepository.saveAll(reports);
    for (SearchHistory searchHistory : searchHistorys) {
      searchHistory.setUseridUsers(user);
      searchHistory.setId(null);
    }
    
    searchHistoryRepository.saveAll(searchHistorys);
    for (UpNextQueue upNextQueue : upNextQueues) {
      upNextQueue.setUseridUsers(user);
      upNextQueue.setId(null);
    }
    
    upNextQueueRepository.saveAll(upNextQueues);
    for (UserStatusHistory userStatusHistory : userStatusHistorys) {
      userStatusHistory.setUseridUsers(user);
      userStatusHistory.setId(null);
    }
    
    userStatusHistoryRepository.saveAll(userStatusHistorys);
    for (VerificationCode verificationCode : verificationCodes) {
      verificationCode.setUseridUsers(user);
      verificationCode.setId(null);
    }
    
    verificationCodeRepository.saveAll(verificationCodes);
    
    
    return user;
  } catch (Exception ex) {
    throw new InternalServerErrorException(
    "Error during atomic creation of User and its details.", ex);
  }
}
  
@Override
@Transactional
public User updateFullUser(Long id, User user, List<Artist> artists, List<Client> clients, List<Comment> comments, List<Download> downloads, List<EventMedia> eventMedias, List<Follow> follows, List<Like> likes, List<ListeningHistory> listeningHistorys, List<NotificationPreference> notificationPreferences, List<Notification> notifications, List<Playlist> playlists, List<Report> reports, List<SearchHistory> searchHistorys, List<UpNextQueue> upNextQueues, List<UserStatusHistory> userStatusHistorys, List<VerificationCode> verificationCodes) {
  try {
    if (!userRepository.existsById(id)) {
      throw new ResourceNotFoundException("User not found with id: " + id);
    }

    user.setId(id);
    user = userRepository.save(user);

    List<Artist> existingArtists = artistRepository.findByUseridUsers(user);

    List<Artist> artistsToDelete = existingArtists.stream()
      .filter(existing -> artists.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!artistsToDelete.isEmpty()) {
      artistRepository.deleteAll(artistsToDelete);
    }

    for (Artist artist : artists) {
      artist.setUseridUsers(user);
      
      if (artist.getId() != null && 
          existingArtists.stream()
            .anyMatch(e -> e.getId().equals(artist.getId()))) {
          artistRepository.save(artist);
      } else {
          artist.setId(null);
          artistRepository.save(artist);
      }
    }
    List<Client> existingClients = clientRepository.findByUseridUsers(user);

    List<Client> clientsToDelete = existingClients.stream()
      .filter(existing -> clients.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!clientsToDelete.isEmpty()) {
      clientRepository.deleteAll(clientsToDelete);
    }

    for (Client client : clients) {
      client.setUseridUsers(user);
      
      if (client.getId() != null && 
          existingClients.stream()
            .anyMatch(e -> e.getId().equals(client.getId()))) {
          clientRepository.save(client);
      } else {
          client.setId(null);
          clientRepository.save(client);
      }
    }
    List<Comment> existingComments = commentRepository.findByAuthoruseridUsers(user);

    List<Comment> commentsToDelete = existingComments.stream()
      .filter(existing -> comments.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!commentsToDelete.isEmpty()) {
      commentRepository.deleteAll(commentsToDelete);
    }

    for (Comment comment : comments) {
      comment.setAuthoruseridUsers(user);
      
      if (comment.getId() != null && 
          existingComments.stream()
            .anyMatch(e -> e.getId().equals(comment.getId()))) {
          commentRepository.save(comment);
      } else {
          comment.setId(null);
          commentRepository.save(comment);
      }
    }
    List<Download> existingDownloads = downloadRepository.findByUseridUsers(user);

    List<Download> downloadsToDelete = existingDownloads.stream()
      .filter(existing -> downloads.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!downloadsToDelete.isEmpty()) {
      downloadRepository.deleteAll(downloadsToDelete);
    }

    for (Download download : downloads) {
      download.setUseridUsers(user);
      
      if (download.getId() != null && 
          existingDownloads.stream()
            .anyMatch(e -> e.getId().equals(download.getId()))) {
          downloadRepository.save(download);
      } else {
          download.setId(null);
          downloadRepository.save(download);
      }
    }
    List<EventMedia> existingEventMedias = eventMediaRepository.findByUploaderuseridUsers(user);

    List<EventMedia> eventMediasToDelete = existingEventMedias.stream()
      .filter(existing -> eventMedias.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!eventMediasToDelete.isEmpty()) {
      eventMediaRepository.deleteAll(eventMediasToDelete);
    }

    for (EventMedia eventMedia : eventMedias) {
      eventMedia.setUploaderuseridUsers(user);
      
      if (eventMedia.getId() != null && 
          existingEventMedias.stream()
            .anyMatch(e -> e.getId().equals(eventMedia.getId()))) {
          eventMediaRepository.save(eventMedia);
      } else {
          eventMedia.setId(null);
          eventMediaRepository.save(eventMedia);
      }
    }
    List<Follow> existingFollows = followRepository.findByClientuseridUsers(user);

    List<Follow> followsToDelete = existingFollows.stream()
      .filter(existing -> follows.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!followsToDelete.isEmpty()) {
      followRepository.deleteAll(followsToDelete);
    }

    for (Follow follow : follows) {
      follow.setClientuseridUsers(user);
      
      if (follow.getId() != null && 
          existingFollows.stream()
            .anyMatch(e -> e.getId().equals(follow.getId()))) {
          followRepository.save(follow);
      } else {
          follow.setId(null);
          followRepository.save(follow);
      }
    }
    List<Like> existingLikes = likeRepository.findByUseridUsers(user);

    List<Like> likesToDelete = existingLikes.stream()
      .filter(existing -> likes.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!likesToDelete.isEmpty()) {
      likeRepository.deleteAll(likesToDelete);
    }

    for (Like like : likes) {
      like.setUseridUsers(user);
      
      if (like.getId() != null && 
          existingLikes.stream()
            .anyMatch(e -> e.getId().equals(like.getId()))) {
          likeRepository.save(like);
      } else {
          like.setId(null);
          likeRepository.save(like);
      }
    }
    List<ListeningHistory> existingListeningHistorys = listeningHistoryRepository.findByUseridUsers(user);

    List<ListeningHistory> listeningHistorysToDelete = existingListeningHistorys.stream()
      .filter(existing -> listeningHistorys.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!listeningHistorysToDelete.isEmpty()) {
      listeningHistoryRepository.deleteAll(listeningHistorysToDelete);
    }

    for (ListeningHistory listeningHistory : listeningHistorys) {
      listeningHistory.setUseridUsers(user);
      
      if (listeningHistory.getId() != null && 
          existingListeningHistorys.stream()
            .anyMatch(e -> e.getId().equals(listeningHistory.getId()))) {
          listeningHistoryRepository.save(listeningHistory);
      } else {
          listeningHistory.setId(null);
          listeningHistoryRepository.save(listeningHistory);
      }
    }
    List<NotificationPreference> existingNotificationPreferences = notificationPreferenceRepository.findByUseridUsers(user);

    List<NotificationPreference> notificationPreferencesToDelete = existingNotificationPreferences.stream()
      .filter(existing -> notificationPreferences.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!notificationPreferencesToDelete.isEmpty()) {
      notificationPreferenceRepository.deleteAll(notificationPreferencesToDelete);
    }

    for (NotificationPreference notificationPreference : notificationPreferences) {
      notificationPreference.setUseridUsers(user);
      
      if (notificationPreference.getId() != null && 
          existingNotificationPreferences.stream()
            .anyMatch(e -> e.getId().equals(notificationPreference.getId()))) {
          notificationPreferenceRepository.save(notificationPreference);
      } else {
          notificationPreference.setId(null);
          notificationPreferenceRepository.save(notificationPreference);
      }
    }
    List<Notification> existingNotifications = notificationRepository.findByUseridUsers(user);

    List<Notification> notificationsToDelete = existingNotifications.stream()
      .filter(existing -> notifications.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!notificationsToDelete.isEmpty()) {
      notificationRepository.deleteAll(notificationsToDelete);
    }

    for (Notification notification : notifications) {
      notification.setUseridUsers(user);
      
      if (notification.getId() != null && 
          existingNotifications.stream()
            .anyMatch(e -> e.getId().equals(notification.getId()))) {
          notificationRepository.save(notification);
      } else {
          notification.setId(null);
          notificationRepository.save(notification);
      }
    }
    List<Playlist> existingPlaylists = playlistRepository.findByOwneruseridUsers(user);

    List<Playlist> playlistsToDelete = existingPlaylists.stream()
      .filter(existing -> playlists.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!playlistsToDelete.isEmpty()) {
      playlistRepository.deleteAll(playlistsToDelete);
    }

    for (Playlist playlist : playlists) {
      playlist.setOwneruseridUsers(user);
      
      if (playlist.getId() != null && 
          existingPlaylists.stream()
            .anyMatch(e -> e.getId().equals(playlist.getId()))) {
          playlistRepository.save(playlist);
      } else {
          playlist.setId(null);
          playlistRepository.save(playlist);
      }
    }
    List<Report> existingReports = reportRepository.findByReporteruseridUsers(user);

    List<Report> reportsToDelete = existingReports.stream()
      .filter(existing -> reports.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!reportsToDelete.isEmpty()) {
      reportRepository.deleteAll(reportsToDelete);
    }

    for (Report report : reports) {
      report.setReporteruseridUsers(user);
      
      if (report.getId() != null && 
          existingReports.stream()
            .anyMatch(e -> e.getId().equals(report.getId()))) {
          reportRepository.save(report);
      } else {
          report.setId(null);
          reportRepository.save(report);
      }
    }
    List<SearchHistory> existingSearchHistorys = searchHistoryRepository.findByUseridUsers(user);

    List<SearchHistory> searchHistorysToDelete = existingSearchHistorys.stream()
      .filter(existing -> searchHistorys.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!searchHistorysToDelete.isEmpty()) {
      searchHistoryRepository.deleteAll(searchHistorysToDelete);
    }

    for (SearchHistory searchHistory : searchHistorys) {
      searchHistory.setUseridUsers(user);
      
      if (searchHistory.getId() != null && 
          existingSearchHistorys.stream()
            .anyMatch(e -> e.getId().equals(searchHistory.getId()))) {
          searchHistoryRepository.save(searchHistory);
      } else {
          searchHistory.setId(null);
          searchHistoryRepository.save(searchHistory);
      }
    }
    List<UpNextQueue> existingUpNextQueues = upNextQueueRepository.findByUseridUsers(user);

    List<UpNextQueue> upNextQueuesToDelete = existingUpNextQueues.stream()
      .filter(existing -> upNextQueues.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!upNextQueuesToDelete.isEmpty()) {
      upNextQueueRepository.deleteAll(upNextQueuesToDelete);
    }

    for (UpNextQueue upNextQueue : upNextQueues) {
      upNextQueue.setUseridUsers(user);
      
      if (upNextQueue.getId() != null && 
          existingUpNextQueues.stream()
            .anyMatch(e -> e.getId().equals(upNextQueue.getId()))) {
          upNextQueueRepository.save(upNextQueue);
      } else {
          upNextQueue.setId(null);
          upNextQueueRepository.save(upNextQueue);
      }
    }
    List<UserStatusHistory> existingUserStatusHistorys = userStatusHistoryRepository.findByUseridUsers(user);

    List<UserStatusHistory> userStatusHistorysToDelete = existingUserStatusHistorys.stream()
      .filter(existing -> userStatusHistorys.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!userStatusHistorysToDelete.isEmpty()) {
      userStatusHistoryRepository.deleteAll(userStatusHistorysToDelete);
    }

    for (UserStatusHistory userStatusHistory : userStatusHistorys) {
      userStatusHistory.setUseridUsers(user);
      
      if (userStatusHistory.getId() != null && 
          existingUserStatusHistorys.stream()
            .anyMatch(e -> e.getId().equals(userStatusHistory.getId()))) {
          userStatusHistoryRepository.save(userStatusHistory);
      } else {
          userStatusHistory.setId(null);
          userStatusHistoryRepository.save(userStatusHistory);
      }
    }
    List<VerificationCode> existingVerificationCodes = verificationCodeRepository.findByUseridUsers(user);

    List<VerificationCode> verificationCodesToDelete = existingVerificationCodes.stream()
      .filter(existing -> verificationCodes.stream()
      .noneMatch(current -> current.getId() != null && current.getId().equals(existing.getId())))
      .collect(Collectors.toList());
    if (!verificationCodesToDelete.isEmpty()) {
      verificationCodeRepository.deleteAll(verificationCodesToDelete);
    }

    for (VerificationCode verificationCode : verificationCodes) {
      verificationCode.setUseridUsers(user);
      
      if (verificationCode.getId() != null && 
          existingVerificationCodes.stream()
            .anyMatch(e -> e.getId().equals(verificationCode.getId()))) {
          verificationCodeRepository.save(verificationCode);
      } else {
          verificationCode.setId(null);
          verificationCodeRepository.save(verificationCode);
      }
    }
    
    
    return user;
  } catch (ResourceNotFoundException ex) {
    throw ex;
  } catch (Exception ex) {
    throw new InternalServerErrorException("Error during atomic update of User and its details.", ex);
  }
}
  
  @Override
  @Transactional
  public void deleteFullUser(Long id) {
  try {
  User user = userRepository.findById(id)
  .orElseThrow(() -> new ResourceNotFoundException(
  "User not found with id: " + id));
        
        List<Artist> artistsToDelete = artistRepository.findByUseridUsers(user);
        
        if (!artistsToDelete.isEmpty()) {
            artistRepository.deleteAll(artistsToDelete);
        }
        List<Client> clientsToDelete = clientRepository.findByUseridUsers(user);
        
        if (!clientsToDelete.isEmpty()) {
            clientRepository.deleteAll(clientsToDelete);
        }
        List<Comment> commentsToDelete = commentRepository.findByAuthoruseridUsers(user);
        
        if (!commentsToDelete.isEmpty()) {
            commentRepository.deleteAll(commentsToDelete);
        }
        List<Download> downloadsToDelete = downloadRepository.findByUseridUsers(user);
        
        if (!downloadsToDelete.isEmpty()) {
            downloadRepository.deleteAll(downloadsToDelete);
        }
        List<EventMedia> eventMediasToDelete = eventMediaRepository.findByUploaderuseridUsers(user);
        
        if (!eventMediasToDelete.isEmpty()) {
            eventMediaRepository.deleteAll(eventMediasToDelete);
        }
        List<Follow> followsToDelete = followRepository.findByClientuseridUsers(user);
        
        if (!followsToDelete.isEmpty()) {
            followRepository.deleteAll(followsToDelete);
        }
        List<Like> likesToDelete = likeRepository.findByUseridUsers(user);
        
        if (!likesToDelete.isEmpty()) {
            likeRepository.deleteAll(likesToDelete);
        }
        List<ListeningHistory> listeningHistorysToDelete = listeningHistoryRepository.findByUseridUsers(user);
        
        if (!listeningHistorysToDelete.isEmpty()) {
            listeningHistoryRepository.deleteAll(listeningHistorysToDelete);
        }
        List<NotificationPreference> notificationPreferencesToDelete = notificationPreferenceRepository.findByUseridUsers(user);
        
        if (!notificationPreferencesToDelete.isEmpty()) {
            notificationPreferenceRepository.deleteAll(notificationPreferencesToDelete);
        }
        List<Notification> notificationsToDelete = notificationRepository.findByUseridUsers(user);
        
        if (!notificationsToDelete.isEmpty()) {
            notificationRepository.deleteAll(notificationsToDelete);
        }
        List<Playlist> playlistsToDelete = playlistRepository.findByOwneruseridUsers(user);
        
        if (!playlistsToDelete.isEmpty()) {
            playlistRepository.deleteAll(playlistsToDelete);
        }
        List<Report> reportsToDelete = reportRepository.findByReporteruseridUsers(user);
        
        if (!reportsToDelete.isEmpty()) {
            reportRepository.deleteAll(reportsToDelete);
        }
        List<SearchHistory> searchHistorysToDelete = searchHistoryRepository.findByUseridUsers(user);
        
        if (!searchHistorysToDelete.isEmpty()) {
            searchHistoryRepository.deleteAll(searchHistorysToDelete);
        }
        List<UpNextQueue> upNextQueuesToDelete = upNextQueueRepository.findByUseridUsers(user);
        
        if (!upNextQueuesToDelete.isEmpty()) {
            upNextQueueRepository.deleteAll(upNextQueuesToDelete);
        }
        List<UserStatusHistory> userStatusHistorysToDelete = userStatusHistoryRepository.findByUseridUsers(user);
        
        if (!userStatusHistorysToDelete.isEmpty()) {
            userStatusHistoryRepository.deleteAll(userStatusHistorysToDelete);
        }
        List<VerificationCode> verificationCodesToDelete = verificationCodeRepository.findByUseridUsers(user);
        
        if (!verificationCodesToDelete.isEmpty()) {
            verificationCodeRepository.deleteAll(verificationCodesToDelete);
        }
        
        
        userRepository.delete(user);
        
    } catch (ResourceNotFoundException ex) {
        throw ex;
    } catch (Exception ex) {
        throw new InternalServerErrorException(
            "Error during atomic deletion of User and its details.", ex);
}
}
  

}
