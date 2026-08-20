package org.example.mozika.services.interfaces;

import org.example.mozika.models.User;
import org.example.mozika.models.Artist;
import org.example.mozika.models.Client;
import org.example.mozika.models.Comment;
import org.example.mozika.models.Download;
import org.example.mozika.models.EventMedia;
import org.example.mozika.models.Follow;
import org.example.mozika.models.Like;
import org.example.mozika.models.ListeningHistory;
import org.example.mozika.models.NotificationPreference;
import org.example.mozika.models.Notification;
import org.example.mozika.models.Playlist;
import org.example.mozika.models.Report;
import org.example.mozika.models.SearchHistory;
import org.example.mozika.models.UpNextQueue;
import org.example.mozika.models.UserStatusHistory;
import org.example.mozika.models.VerificationCode;

import org.example.mozika.models.dto.UserSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface UserService {
    Page<User> getAllUser(Pageable pageable);

    Page<User> getAllUser(Pageable pageable, UserSearch object);

    User getUserById(Long id);

    public String exportUserToCSV(List<User> user);

    

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);
    

    User createFullUser(User user,List<Artist> artists,List<Client> clients,List<Comment> comments,List<Download> downloads,List<EventMedia> eventMedias,List<Follow> follows,List<Like> likes,List<ListeningHistory> listeningHistorys,List<NotificationPreference> notificationPreferences,List<Notification> notifications,List<Playlist> playlists,List<Report> reports,List<SearchHistory> searchHistorys,List<UpNextQueue> upNextQueues,List<UserStatusHistory> userStatusHistorys,List<VerificationCode> verificationCodes);
    User updateFullUser(Long id, User user,List<Artist> artists,List<Client> clients,List<Comment> comments,List<Download> downloads,List<EventMedia> eventMedias,List<Follow> follows,List<Like> likes,List<ListeningHistory> listeningHistorys,List<NotificationPreference> notificationPreferences,List<Notification> notifications,List<Playlist> playlists,List<Report> reports,List<SearchHistory> searchHistorys,List<UpNextQueue> upNextQueues,List<UserStatusHistory> userStatusHistorys,List<VerificationCode> verificationCodes);
    void deleteFullUser(Long id);
}
