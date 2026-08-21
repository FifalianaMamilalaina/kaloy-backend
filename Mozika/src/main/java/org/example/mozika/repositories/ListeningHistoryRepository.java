package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.ListeningHistory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.User;
import org.example.mozika.models.Song;



public interface ListeningHistoryRepository extends JpaRepository<ListeningHistory, Long>, JpaSpecificationExecutor<ListeningHistory> {

List<ListeningHistory> findByUseridUsers(User useridUsers);
List<ListeningHistory> findBySongidSongs(Song songidSongs);



}
