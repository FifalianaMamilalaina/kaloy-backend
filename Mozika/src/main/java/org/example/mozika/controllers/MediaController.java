package org.example.mozika.controllers;

import lombok.RequiredArgsConstructor;
import org.example.mozika.dto.RestResponse;
import org.example.mozika.models.dto.MediaStreamUrlsDto;
import org.example.mozika.models.dto.SongPlayerDto;
import org.example.mozika.services.interfaces.MediaService;
import org.example.mozika.services.interfaces.PlaylistMobileService;
import org.example.mozika.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;
    private final PlaylistMobileService playlistMobileService;

    @Value("${kaloy.uploads.dossier}")
    private String uploadsDirectory;

    @Value("${app.media-base-url:}")
    private String mediaBaseUrl;

    @GetMapping("/songs/{songId}/urls")
    public ResponseEntity<RestResponse<MediaStreamUrlsDto>> getSongStreamUrls(
            @PathVariable Long songId) {
        MediaStreamUrlsDto dto = mediaService.getSongStreamUrls(songId);
        return ResponseEntity.ok(
                RestResponse.buildSuccessResponse(HttpStatus.OK, "URLs média générées avec succès", dto));
    }

    @GetMapping("/songs/{songId}/player")
    public ResponseEntity<RestResponse<SongPlayerDto>> getSongPlayerDetails(
            @PathVariable Long songId) {
        SongPlayerDto dto = mediaService.getSongPlayerDetails(songId);
        return ResponseEntity.ok(
                RestResponse.buildSuccessResponse(HttpStatus.OK, "Détails du lecteur récupérés avec succès", dto));
    }

    @GetMapping("/songs/{songId}/stream/{mediaType}")
    public ResponseEntity<InputStreamResource> stream(
            @PathVariable Long songId,
            @PathVariable String mediaType,
            @org.springframework.web.bind.annotation.RequestHeader(value = HttpHeaders.RANGE, required = false) String range) {
        var song = mediaService.getSong(songId);
        String objectName = switch (mediaType) {
            case "audio" -> song.getAudioUrl();
            case "karaoke" -> song.getKaraokeAudioUrl();
            case "playback" -> song.getPlaybackUrl();
            default -> throw new IllegalArgumentException("Type média inconnu");
        };
        var metadata = mediaService.statObject(objectName);
        long size = metadata.size();
        long start = 0;
        long end = size - 1;
        if (range != null && range.startsWith("bytes=")) {
            Matcher matcher = Pattern.compile("bytes=(\\d+)-(\\d*)").matcher(range);
            if (matcher.matches()) {
                start = Long.parseLong(matcher.group(1));
                if (!matcher.group(2).isBlank())
                    end = Long.parseLong(matcher.group(2));
                end = Math.min(end, size - 1);
            }
        }
        long length = end - start + 1;
        InputStream stream = range == null
                ? mediaService.openObject(objectName)
                : mediaService.openObject(objectName, start, length);
        MediaType contentType = MediaType.parseMediaType(
                metadata.contentType() == null ? "application/octet-stream" : metadata.contentType());
        ResponseEntity.BodyBuilder response = range == null
                ? ResponseEntity.ok()
                : ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + size);
        return response
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .contentType(contentType)
                .contentLength(length)
                .body(new InputStreamResource(stream));
    }

    @GetMapping("/songs/{songId}/offline-download")
    public ResponseEntity<InputStreamResource> offlineDownload(
            @PathVariable Long songId,
            @AuthenticationPrincipal UserDetails userDetails) throws java.io.IOException {
        playlistMobileService.authorizeOfflineSongDownload(userDetails.getUsername(), songId);
        String storedUrl = mediaService.getSong(songId).getAudioUrl();
        String uploadPath = storedUrl;
        if (storedUrl.startsWith("http://") || storedUrl.startsWith("https://")) {
            URI storedUri = URI.create(storedUrl);
            URI backendUri = URI.create(mediaBaseUrl);
            if (!java.util.Objects.equals(storedUri.getScheme(), backendUri.getScheme())
                    || !java.util.Objects.equals(storedUri.getAuthority(), backendUri.getAuthority())) {
                throw new ResourceNotFoundException("Offline upload not found for song: " + songId);
            }
            String storedPath = storedUri.getPath();
            int uploadIndex = storedPath == null ? -1 : storedPath.indexOf("/uploads/");
            uploadPath = uploadIndex < 0 ? null : storedPath.substring(uploadIndex);
        }
        if (uploadPath == null || !uploadPath.startsWith("/uploads/")) {
            throw new ResourceNotFoundException("Offline upload not found for song: " + songId);
        }
        Path root = Paths.get(uploadsDirectory).toAbsolutePath().normalize();
        Path file = root.resolve(uploadPath.substring("/uploads/".length())).normalize();
        if (!file.startsWith(root) || !Files.isRegularFile(file)) {
            throw new ResourceNotFoundException("Offline upload not found for song: " + songId);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(Files.size(file))
                .body(new InputStreamResource(Files.newInputStream(file)));
    }
}
