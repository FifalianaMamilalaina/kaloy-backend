package org.example.mozika.models.dto;

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

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserFullDto {
    private User user;
    
    private List<Artist> artists;
    private List<Client> clients;
    private List<Comment> comments;
    private List<Download> downloads;
    private List<EventMedia> eventMedias;
    private List<Follow> follows;
    private List<Like> likes;
    private List<ListeningHistory> listeningHistorys;
    private List<NotificationPreference> notificationPreferences;
    private List<Notification> notifications;
    private List<Playlist> playlists;
    private List<Report> reports;
    private List<SearchHistory> searchHistorys;
    private List<UpNextQueue> upNextQueues;
    private List<UserStatusHistory> userStatusHistorys;
    private List<VerificationCode> verificationCodes;
    
}
