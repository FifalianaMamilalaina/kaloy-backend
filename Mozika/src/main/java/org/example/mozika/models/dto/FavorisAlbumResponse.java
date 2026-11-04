package org.example.mozika.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.mozika.models.Album;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavorisAlbumResponse {
    private Long likeId;
    private Album album;
    private LocalDateTime likedAt;
}
