package org.example.mozika.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatsFavorisResponse {
    private final long chansons;
    private final long albums;
    private final long artistes;
}
