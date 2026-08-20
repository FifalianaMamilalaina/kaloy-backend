package org.example.mozika.models.dto;

import org.example.mozika.models.Venue;

import org.example.mozika.models.Concert;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VenueFullDto {
    private Venue venue;
    
    private List<Concert> concerts;
    
}
