package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.Playlist;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface PlaylistRepository extends JpaRepository<Playlist, Long>, JpaSpecificationExecutor<Playlist> {

List<Playlist> findByOwneruseridUsers(User owneruseridUsers);

@Query("""
	select p from Playlist p
	where upper(p.visibilityidPlaylistVisibilities.name) = 'PUBLIC'
	""")
Page<Playlist> findPublicPlaylists(Pageable pageable);

@Query("""
	select p from Playlist p
	where upper(p.visibilityidPlaylistVisibilities.name) = 'PUBLIC'
	  and lower(p.name) like lower(concat('%', :search, '%'))
	""")
Page<Playlist> searchPublicPlaylists(@Param("search") String search, Pageable pageable);

}
