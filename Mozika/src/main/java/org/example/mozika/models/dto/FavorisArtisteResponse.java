package org.example.mozika.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.mozika.models.Artist;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavorisArtisteResponse {
    private Long likeId;
    private Artist artiste;
    private LocalDateTime likedAt;
}
