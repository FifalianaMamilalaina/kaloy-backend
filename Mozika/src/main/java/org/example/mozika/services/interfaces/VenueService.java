package org.example.mozika.services.interfaces;

import org.example.mozika.models.Venue;
import org.example.mozika.models.Concert;

import org.example.mozika.models.dto.VenueSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;    
import java.util.List;


public interface VenueService {
    Page<Venue> getAllVenue(Pageable pageable);

    Page<Venue> getAllVenue(Pageable pageable, VenueSearch object);

    Venue getVenueById(Long id);

    public String exportVenueToCSV(List<Venue> venue);

    

    Venue createVenue(Venue venue);

    Venue updateVenue(Long id, Venue venue);

    void deleteVenue(Long id);
    

    Venue createFullVenue(Venue venue,List<Concert> concerts);
    Venue updateFullVenue(Long id, Venue venue,List<Concert> concerts);
    void deleteFullVenue(Long id);
}
