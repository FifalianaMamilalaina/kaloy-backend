package org.example.mozika.services.interfaces;

import org.example.mozika.models.EventMedia;
import org.example.mozika.models.dto.EventMediaSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface EventMediaService {
    Page<EventMedia> getAllEventMedia(Pageable pageable);

    Page<EventMedia> getAllEventMedia(Pageable pageable, EventMediaSearch object);

    EventMedia getEventMediaById(Long id);

    public String exportEventMediaToCSV(List<EventMedia> eventMedia);

    

    EventMedia createEventMedia(EventMedia eventMedia);

    EventMedia updateEventMedia(Long id, EventMedia eventMedia);

    void deleteEventMedia(Long id);
    

}
