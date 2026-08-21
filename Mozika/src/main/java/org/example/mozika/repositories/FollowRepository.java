package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.Follow;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.User;
import org.example.mozika.models.Artist;



public interface FollowRepository extends JpaRepository<Follow, Long>, JpaSpecificationExecutor<Follow> {

List<Follow> findByClientuseridUsers(User clientuseridUsers);
List<Follow> findByArtistidArtists(Artist artistidArtists);



}
