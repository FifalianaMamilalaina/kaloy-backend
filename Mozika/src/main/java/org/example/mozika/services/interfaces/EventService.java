package org.example.mozika.services.interfaces;

import org.example.mozika.models.Event;
import org.example.mozika.models.Concert;
import org.example.mozika.models.EventMedia;

import org.example.mozika.models.dto.EventSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface EventService {
    Page<Event> getAllEvent(Pageable pageable);

    Page<Event> getAllEvent(Pageable pageable, EventSearch object);

    Event getEventById(Long id);

    public String exportEventToCSV(List<Event> event);

    

    Event createEvent(Event event);

    Event updateEvent(Long id, Event event);

    void deleteEvent(Long id);
    

    Event createFullEvent(Event event,List<Concert> concerts,List<EventMedia> eventMedias);
    Event updateFullEvent(Long id, Event event,List<Concert> concerts,List<EventMedia> eventMedias);
    void deleteFullEvent(Long id);
}
