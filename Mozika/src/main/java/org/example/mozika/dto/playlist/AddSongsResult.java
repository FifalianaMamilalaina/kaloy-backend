package org.example.mozika.dto.playlist;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddSongsResult {
    private List<PlaylistSongMobileResponse> added;
    private List<Long> alreadyPresent;
    private List<Long> notFound;

    public AddSongsResult(List<PlaylistSongMobileResponse> added,
            List<Long> alreadyPresent, List<Long> notFound) {
        this.added = added;
        this.alreadyPresent = alreadyPresent;
        this.notFound = notFound;
    }
}
