package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.Event;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.Artist;



public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

List<Event> findByCreatedbyartistidArtists(Artist createdbyartistidArtists);



}
