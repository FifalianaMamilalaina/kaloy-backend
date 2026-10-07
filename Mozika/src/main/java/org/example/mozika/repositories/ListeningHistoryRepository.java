package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.ListeningHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.example.mozika.models.User;
import org.example.mozika.models.Song;



public interface ListeningHistoryRepository extends JpaRepository<ListeningHistory, Long>, JpaSpecificationExecutor<ListeningHistory> {

List<ListeningHistory> findByUseridUsers(User useridUsers);
List<ListeningHistory> findBySongidSongs(Song songidSongs);

@Query("SELECT lh FROM ListeningHistory lh WHERE lh.useridUsers.id = :userId AND lh.songidSongs.id = :songId ORDER BY lh.listenedAt DESC")
List<ListeningHistory> findByUserIdAndSongId(@Param("userId") Long userId, @Param("songId") Long songId);

}
