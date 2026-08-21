package org.example.mozika.models.dto;

import org.example.mozika.models.Album;

import org.example.mozika.models.Song;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AlbumFullDto {
    private Album album;
    
    private List<Song> songs;
    
}
