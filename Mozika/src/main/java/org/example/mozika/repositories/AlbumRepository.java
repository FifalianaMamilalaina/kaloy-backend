package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.Album;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.Artist;



public interface AlbumRepository extends JpaRepository<Album, Long>, JpaSpecificationExecutor<Album> {

List<Album> findByArtistidArtists(Artist artistidArtists);



}
