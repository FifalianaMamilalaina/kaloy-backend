package org.example.mozika.exception;

import java.util.List;

public class NondownloadableSongsException extends RuntimeException {
    private final List<Long> songIds;

    public NondownloadableSongsException(List<Long> songIds) {
        super("Playlist contains songs that are not available for offline download");
        this.songIds = List.copyOf(songIds);
    }

    public List<Long> getSongIds() {
        return songIds;
    }
}
