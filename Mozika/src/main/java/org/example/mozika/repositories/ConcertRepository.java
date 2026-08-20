package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.Concert;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.Event;
import org.example.mozika.models.Artist;
import org.example.mozika.models.Venue;



public interface ConcertRepository extends JpaRepository<Concert, Long>, JpaSpecificationExecutor<Concert> {

List<Concert> findByEventidEvents(Event eventidEvents);
List<Concert> findByArtistidArtists(Artist artistidArtists);
List<Concert> findByVenueidVenues(Venue venueidVenues);



}
