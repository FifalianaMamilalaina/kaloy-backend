package org.example.mozika.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.mozika.models.Song;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavorisSongResponse {
    private Long likeId;
    private Song song;
    private LocalDateTime likedAt;
}
