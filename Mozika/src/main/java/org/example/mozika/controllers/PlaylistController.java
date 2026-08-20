package org.example.mozika.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.example.mozika.models.Playlist;
import org.example.mozika.models.dto.PlaylistSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.PlaylistService;
import org.example.mozika.services.interfaces.DownloadService;
import org.example.mozika.models.dto.DownloadSearch;
import org.example.mozika.models.Download;
import org.example.mozika.services.interfaces.PlaylistSongService;
import org.example.mozika.models.dto.PlaylistSongSearch;
import org.example.mozika.models.PlaylistSong;


import org.example.mozika.models.dto.PlaylistFullDto;

import org.example.mozika.utils.WebUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;

import org.example.mozika.dto.RestResponse;
import org.example.mozika.exception.ResourceNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/playlists")
@Tag(name = "Playlist", description = "Playlist Management APIs")
public class PlaylistController  {
	private final PlaylistService playlistService;
private final DownloadService downloadService;
private final PlaylistSongService playlistsongService;


	public PlaylistController(PlaylistService playlistService, DownloadService downloadService, PlaylistSongService playlistsongService) {
	   this.playlistService = playlistService;
	this.downloadService = downloadService;
	this.playlistsongService = playlistsongService;
	
	}

	@Operation(
	    summary = "Retrieve all playlist",
	    description = "Get a paginated and sorted list of playlist items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of playlist",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No playlist found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<Playlist>>> getAllPlaylists(
	    @Parameter(description = "Page number (0-indexed)", example = "0")
	    @RequestParam(defaultValue = "0") int page,
	
	    @Parameter(description = "Size of the page", example = "10")
	    @RequestParam(defaultValue = "10") int size,
	
	    @Parameter(
	        description = "Sorting criteria in the format: property,(asc|desc). "
	                + "Multiple sort criteria can be passed using semicolon separator. "
	                + "Example: id,asc;name,desc",
	        example = "id,asc"
	    )
	    @RequestParam(defaultValue = "id,asc") String sortParam) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<Playlist> playlists = playlistService.getAllPlaylist(pageable);
	
	    String message = "playlist retrieved successfully";
	
	    if(!playlists.hasContent()) {
	            message = "No playlist found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Playlist>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            playlists
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all playlist",
	    description = "Get a paginated and sorted list of playlist items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of playlist",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No playlist found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<Playlist>>> getAllPlaylists(
	    @Parameter(description = "Page number (0-indexed)", example = "0")
	    @RequestParam(defaultValue = "0") int page,
	
	    @Parameter(description = "Size of the page", example = "10")
	    @RequestParam(defaultValue = "10") int size,
	
	    @Parameter(
	        description = "Sorting criteria in the format: property,(asc|desc). "
	                + "Multiple sort criteria can be passed using semicolon separator. "
	                + "Example: id,asc;name,desc",
	        example = "id,asc"
	    )
	    @RequestParam(defaultValue = "id,asc") String sortParam,
	    @RequestBody PlaylistSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<Playlist> playlists = playlistService.getAllPlaylist(pageable, object);
	
	    String message = "playlist retrieved successfully";
	
	    if(!playlists.hasContent()) {
	            message = "No playlist found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Playlist>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            playlists
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get playlist by ID",
	    description = "Retrieve a specific playlist item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the playlist",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Playlist not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<Playlist>> getPlaylistById(
	    @Parameter(description = "ID of the playlist to retrieve", required = true)
	    @PathVariable Long id
	) {
	    Playlist playlist = playlistService.getPlaylistById(id);
	    RestResponse<Playlist> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "playlist retrieved successfully", playlist);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Playlist to CSV",
	     description = "Generate a CSV file from a provided list of Playlist objects."
	)
	@ApiResponses({
	   @ApiResponse(
	   responseCode = "200",
	   description = "CSV file successfully generated",
	   content = @Content(mediaType = "text/csv")
	   ),
	   @ApiResponse(
	   responseCode = "400",
	   description = "Invalid or empty list provided"
	   )
	})
	@PostMapping(value = "/export/csv", consumes = "application/json", produces = "text/csv")
	public ResponseEntity<byte[]> exportPlaylistToCsv(@RequestBody List<Playlist> playlist) {
	  
	  String csvContent = playlistService.exportPlaylistToCSV(playlist);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new playlist",

	    description = "Create a new playlist and its details. The operation is atomic: all or nothing."
	)
	@ApiResponses({
	   @ApiResponse(
	     responseCode = "201",
	     description = "Playlist created successfully",
	     content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
	   ),
	   @ApiResponse(
	     responseCode = "400",
	     description = "Validation failed for one or more entities"
	   ),
	   @ApiResponse(
	     responseCode = "500",
	     description = "Internal server error"
	   )
	})
	@PostMapping
	public ResponseEntity<?> createFullPlaylist(
	    @Parameter(description = "Playlist object to be created with downloads{{#unless @last}}, {{/unless}}playlistSongs{{#unless @last}}, {{/unless}}", required = true)
	    @RequestBody @Valid PlaylistFullDto dto,
	    BindingResult bindingResult
	) {
	  if (bindingResult.hasErrors()) {
	    HashMap<String, String> errors = new HashMap<>();
	    bindingResult.getFieldErrors().forEach(error -> {
	      	errors.put(error.getField(), error.getDefaultMessage());
	    });
	    RestResponse<HashMap<String, String>> errorResponse =
	    RestResponse.buildErrorResponse(HttpStatus.BAD_REQUEST,
	    "Validation failed", errors);
	    return ResponseEntity.badRequest().body(errorResponse);
	 }
	  
	  try {
	    Playlist playlist = dto.getPlaylist();
	    List<Download> downloads = dto.getDownloads();
	    List<PlaylistSong> playlistSongs = dto.getPlaylistSongs();
	    
	    Playlist createdPlaylist = playlistService.createFullPlaylist(playlist, downloads, playlistSongs);
	    RestResponse<Playlist> response = RestResponse.buildSuccessResponse(
	      HttpStatus.CREATED,
	      "Playlist and its details created successfully (Atomic operation)",
	      createdPlaylist
	    );
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	  } catch (DataIntegrityViolationException ex) {
	    String message = String.format("A database error occurred during the transaction (e.g., constraint violation): %s",
	      	ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage());
	    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
	      HttpStatus.BAD_REQUEST,
	      message,
	      null
	    );
	    return ResponseEntity.badRequest().body(errorResponse);
	  } catch (Exception ex) {
	    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
	      HttpStatus.INTERNAL_SERVER_ERROR,
	      String.format("An unexpected error occurred while creating the playlist: %s", ex.getMessage()),
	      null
	    );
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	  }
	}

 
	@Operation(
	    summary = "Update existing playlist",

          description = "Update an existing playlist and its details. The operation is atomic: all or nothing."
      )
      @ApiResponses({
      @ApiResponse(
      responseCode = "200",
      description = "Playlist updated successfully",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
      ),
      @ApiResponse(
      responseCode = "404",
      description = "Playlist not found with the provided ID"
      ),
      @ApiResponse(
      responseCode = "400",
      description = "Validation failed or database constraint violation"
      ),
      @ApiResponse(
      responseCode = "500",
      description = "Internal server error"
      )
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFullPlaylist(
      @Parameter(description = "ID of the playlist to update", required = true)
      @PathVariable Long id,
      @Parameter(description = "Updated playlist object with downloads{{#unless @last}}, {{/unless}}playlistSongs{{#unless @last}}, {{/unless}}", required = true)
      @RequestBody @Valid PlaylistFullDto dto,
      BindingResult bindingResult
    ) {
      if (bindingResult.hasErrors()) {
        HashMap<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> {
        errors.put(error.getField(), error.getDefaultMessage());
        });
        RestResponse<HashMap<String, String>> errorResponse =
        RestResponse.buildErrorResponse(HttpStatus.BAD_REQUEST,
        "Validation failed", errors);
        
        return ResponseEntity.badRequest().body(errorResponse);
      }
      
      try {
        // Check if playlist exists
        Playlist existingPlaylist = playlistService.getPlaylistById(id);
        if (existingPlaylist == null) {
          RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
          HttpStatus.NOT_FOUND,
          String.format("Playlist not found with id: %s", id),
          null
          );
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        
        Playlist playlist = dto.getPlaylist();
        List<Download> downloads = dto.getDownloads();
        List<PlaylistSong> playlistSongs = dto.getPlaylistSongs();
        
        
        Playlist updatedPlaylist = playlistService.updateFullPlaylist(
            id, playlist, downloads, playlistSongs
        );
        
        RestResponse<Playlist> response = RestResponse.buildSuccessResponse(
            HttpStatus.OK,
            "Playlist and its details updated successfully (Atomic operation)",
            updatedPlaylist
        );
        return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
        RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.NOT_FOUND,
            String.format("Playlist not found with id: %s", id),
            null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (DataIntegrityViolationException ex) {
        String message = String.format("A database error occurred during the transaction (e.g., constraint violation): %s", 
            ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage());
        RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            message,
            null
        );
        return ResponseEntity.badRequest().body(errorResponse);
      } catch (Exception ex) {
        RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            String.format("An unexpected error occurred while updating the playlist: %s", ex.getMessage()),
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
      }
    }


	@Operation(
	    summary = "Delete playlist",

          description = "Delete an existing playlist and all its associated details. The operation is atomic: all or nothing."
      )
      @ApiResponses({
        @ApiResponse(
          responseCode = "200",
          description = "Playlist deleted successfully"
        ),
        @ApiResponse(
          responseCode = "404",
          description = "Playlist not found with the provided ID"
        ),
        @ApiResponse(
          responseCode = "500",
          description = "Internal server error"
        )
      })
      @DeleteMapping("/{id}")
      public ResponseEntity<RestResponse<Void>> deleteFullPlaylist(
        @Parameter(description = "ID of the playlist to delete", required = true)
        @PathVariable Long id
      ) {
        try {
          playlistService.deleteFullPlaylist(id);
          return ResponseEntity.ok(
            RestResponse.buildSuccessResponse(
              HttpStatus.OK,
              "Playlist deleted successfully",
              null
            )
          );
        } catch (ResourceNotFoundException ex) {
          RestResponse<Void> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.NOT_FOUND,
            String.format("Playlist not found with id: %s", id),
            null
          );
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
          RestResponse<Void> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            String.format("Error while performing atomic deletion of playlist: %s", ex.getMessage()),
            null
          );
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
      } 

@Operation(
  summary = "Get downloads attached with a playlist ID",
  description = "Retrieve a list of downloads from a playlist item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "Downloads retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "Playlist not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/downloads")
public ResponseEntity<?> getDownloadsByPlaylist(
  @Parameter(description = "ID of the playlist to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  Playlist playlist = playlistService.getPlaylistById(id);
  if (playlist == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("Playlist not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  DownloadSearch object = new DownloadSearch();
  object.setPlaylistidPlaylists(playlist);
  
  Page<Download> downloadsData = downloadService.getAllDownload(pageable, object);
  
  RestResponse<Page<Download>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "Downloads retrieved successfully",
    downloadsData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving playlist: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get playlistSongs attached with a playlist ID",
  description = "Retrieve a list of playlistSongs from a playlist item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "PlaylistSongs retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "Playlist not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/playlistsongs")
public ResponseEntity<?> getPlaylistSongsByPlaylist(
  @Parameter(description = "ID of the playlist to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  Playlist playlist = playlistService.getPlaylistById(id);
  if (playlist == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("Playlist not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  PlaylistSongSearch object = new PlaylistSongSearch();
  object.setPlaylistidPlaylists(playlist);
  
  Page<PlaylistSong> playlistSongsData = playlistsongService.getAllPlaylistSong(pageable, object);
  
  RestResponse<Page<PlaylistSong>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "PlaylistSongs retrieved successfully",
    playlistSongsData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving playlist: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}


}
