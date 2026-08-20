package org.example.mozika.models.dto;

import org.example.mozika.models.Genre;

import org.example.mozika.models.SongGenre;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GenreFullDto {
    private Genre genre;
    
    private List<SongGenre> songGenres;
    
}
