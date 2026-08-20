package org.example.mozika.services.interfaces;

import org.example.mozika.models.EventModerationStatuse;
import org.example.mozika.models.dto.EventModerationStatuseSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface EventModerationStatuseService {
    Page<EventModerationStatuse> getAllEventModerationStatuse(Pageable pageable);

    Page<EventModerationStatuse> getAllEventModerationStatuse(Pageable pageable, EventModerationStatuseSearch object);

    EventModerationStatuse getEventModerationStatuseById(Long id);

    public String exportEventModerationStatuseToCSV(List<EventModerationStatuse> eventModerationStatuse);

    

    EventModerationStatuse createEventModerationStatuse(EventModerationStatuse eventModerationStatuse);

    EventModerationStatuse updateEventModerationStatuse(Long id, EventModerationStatuse eventModerationStatuse);

    void deleteEventModerationStatuse(Long id);
    

}
