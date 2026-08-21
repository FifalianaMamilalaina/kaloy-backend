package org.example.mozika.models.dto;

import org.example.mozika.models.Event;

import org.example.mozika.models.Concert;
import org.example.mozika.models.EventMedia;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventFullDto {
    private Event event;
    
    private List<Concert> concerts;
    private List<EventMedia> eventMedias;
    
}
