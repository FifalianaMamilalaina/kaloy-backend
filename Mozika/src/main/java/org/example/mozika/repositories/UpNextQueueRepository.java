package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.UpNextQueue;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.User;
import org.example.mozika.models.Song;



public interface UpNextQueueRepository extends JpaRepository<UpNextQueue, Long>, JpaSpecificationExecutor<UpNextQueue> {

List<UpNextQueue> findByUseridUsers(User useridUsers);
List<UpNextQueue> findBySongidSongs(Song songidSongs);



}
