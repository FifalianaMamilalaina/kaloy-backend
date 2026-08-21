package org.example.mozika.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.mozika.models.EventMedia;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.example.mozika.models.Event;
import org.example.mozika.models.User;



public interface EventMediaRepository extends JpaRepository<EventMedia, Long>, JpaSpecificationExecutor<EventMedia> {

List<EventMedia> findByEventidEvents(Event eventidEvents);
List<EventMedia> findByUploaderuseridUsers(User uploaderuseridUsers);



}
