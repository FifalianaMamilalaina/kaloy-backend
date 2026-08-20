package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.EditorialPlaylist;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.Artist;



public interface EditorialPlaylistRepository extends JpaRepository<EditorialPlaylist, Long>, JpaSpecificationExecutor<EditorialPlaylist> {

List<EditorialPlaylist> findByArtistidArtists(Artist artistidArtists);



}
