package org.example.mozika.controllers;

import lombok.RequiredArgsConstructor;
import org.example.mozika.dto.RestResponse;
import org.example.mozika.models.dto.MediaStreamUrlsDto;
import org.example.mozika.services.interfaces.MediaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @GetMapping("/songs/{songId}/urls")
    public ResponseEntity<RestResponse<MediaStreamUrlsDto>> getSongStreamUrls(
            @PathVariable Long songId) {
        MediaStreamUrlsDto dto = mediaService.getSongStreamUrls(songId);
        return ResponseEntity.ok(
                RestResponse.buildSuccessResponse(HttpStatus.OK, "URLs média générées avec succès", dto)
        );
    }
}
