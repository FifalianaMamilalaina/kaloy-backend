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
import org.example.mozika.models.Song;
import org.example.mozika.models.dto.SongSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.SongService;
import org.example.mozika.services.interfaces.EditorialPlaylistSongService;
import org.example.mozika.models.dto.EditorialPlaylistSongSearch;
import org.example.mozika.models.EditorialPlaylistSong;
import org.example.mozika.services.interfaces.ListeningHistoryService;
import org.example.mozika.models.dto.ListeningHistorySearch;
import org.example.mozika.models.ListeningHistory;
import org.example.mozika.services.interfaces.PlaylistSongService;
import org.example.mozika.models.dto.PlaylistSongSearch;
import org.example.mozika.models.PlaylistSong;
import org.example.mozika.services.interfaces.SongGenreService;
import org.example.mozika.models.dto.SongGenreSearch;
import org.example.mozika.models.SongGenre;
import org.example.mozika.services.interfaces.UpNextQueueService;
import org.example.mozika.models.dto.UpNextQueueSearch;
import org.example.mozika.models.UpNextQueue;


import org.example.mozika.models.dto.SongFullDto;

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
@RequestMapping("/songs")
@Tag(name = "Song", description = "Song Management APIs")
public class SongController  {
	private final SongService songService;
private final EditorialPlaylistSongService editorialplaylistsongService;
private final ListeningHistoryService listeninghistoryService;
private final PlaylistSongService playlistsongService;
private final SongGenreService songgenreService;
private final UpNextQueueService upnextqueueService;


	public SongController(SongService songService, EditorialPlaylistSongService editorialplaylistsongService, ListeningHistoryService listeninghistoryService, PlaylistSongService playlistsongService, SongGenreService songgenreService, UpNextQueueService upnextqueueService) {
	   this.songService = songService;
	this.editorialplaylistsongService = editorialplaylistsongService;
	this.listeninghistoryService = listeninghistoryService;
	this.playlistsongService = playlistsongService;
	this.songgenreService = songgenreService;
	this.upnextqueueService = upnextqueueService;
	
	}

	@Operation(
	    summary = "Retrieve all song",
	    description = "Get a paginated and sorted list of song items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of song",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No song found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<Song>>> getAllSongs(
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
	    Page<Song> songs = songService.getAllSong(pageable);
	
	    String message = "song retrieved successfully";
	
	    if(!songs.hasContent()) {
	            message = "No song found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Song>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            songs
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all song",
	    description = "Get a paginated and sorted list of song items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of song",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No song found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<Song>>> getAllSongs(
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
	    @RequestBody SongSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<Song> songs = songService.getAllSong(pageable, object);
	
	    String message = "song retrieved successfully";
	
	    if(!songs.hasContent()) {
	            message = "No song found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Song>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            songs
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get song by ID",
	    description = "Retrieve a specific song item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the song",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Song not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<Song>> getSongById(
	    @Parameter(description = "ID of the song to retrieve", required = true)
	    @PathVariable Long id
	) {
	    Song song = songService.getSongById(id);
	    RestResponse<Song> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "song retrieved successfully", song);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Song to CSV",
	     description = "Generate a CSV file from a provided list of Song objects."
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
	public ResponseEntity<byte[]> exportSongToCsv(@RequestBody List<Song> song) {
	  
	  String csvContent = songService.exportSongToCSV(song);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new song",

	    description = "Create a new song and its details. The operation is atomic: all or nothing."
	)
	@ApiResponses({
	   @ApiResponse(
	     responseCode = "201",
	     description = "Song created successfully",
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
	public ResponseEntity<?> createFullSong(
	    @Parameter(description = "Song object to be created with editorialPlaylistSongs{{#unless @last}}, {{/unless}}listeningHistorys{{#unless @last}}, {{/unless}}playlistSongs{{#unless @last}}, {{/unless}}songGenres{{#unless @last}}, {{/unless}}upNextQueues{{#unless @last}}, {{/unless}}", required = true)
	    @RequestBody @Valid SongFullDto dto,
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
	    Song song = dto.getSong();
	    List<EditorialPlaylistSong> editorialPlaylistSongs = dto.getEditorialPlaylistSongs();
	    List<ListeningHistory> listeningHistorys = dto.getListeningHistorys();
	    List<PlaylistSong> playlistSongs = dto.getPlaylistSongs();
	    List<SongGenre> songGenres = dto.getSongGenres();
	    List<UpNextQueue> upNextQueues = dto.getUpNextQueues();
	    
	    Song createdSong = songService.createFullSong(song, editorialPlaylistSongs, listeningHistorys, playlistSongs, songGenres, upNextQueues);
	    RestResponse<Song> response = RestResponse.buildSuccessResponse(
	      HttpStatus.CREATED,
	      "Song and its details created successfully (Atomic operation)",
	      createdSong
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
	      String.format("An unexpected error occurred while creating the song: %s", ex.getMessage()),
	      null
	    );
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	  }
	}

 
	@Operation(
	    summary = "Update existing song",

          description = "Update an existing song and its details. The operation is atomic: all or nothing."
      )
      @ApiResponses({
      @ApiResponse(
      responseCode = "200",
      description = "Song updated successfully",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
      ),
      @ApiResponse(
      responseCode = "404",
      description = "Song not found with the provided ID"
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
    public ResponseEntity<?> updateFullSong(
      @Parameter(description = "ID of the song to update", required = true)
      @PathVariable Long id,
      @Parameter(description = "Updated song object with editorialPlaylistSongs{{#unless @last}}, {{/unless}}listeningHistorys{{#unless @last}}, {{/unless}}playlistSongs{{#unless @last}}, {{/unless}}songGenres{{#unless @last}}, {{/unless}}upNextQueues{{#unless @last}}, {{/unless}}", required = true)
      @RequestBody @Valid SongFullDto dto,
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
        // Check if song exists
        Song existingSong = songService.getSongById(id);
        if (existingSong == null) {
          RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
          HttpStatus.NOT_FOUND,
          String.format("Song not found with id: %s", id),
          null
          );
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        
        Song song = dto.getSong();
        List<EditorialPlaylistSong> editorialPlaylistSongs = dto.getEditorialPlaylistSongs();
        List<ListeningHistory> listeningHistorys = dto.getListeningHistorys();
        List<PlaylistSong> playlistSongs = dto.getPlaylistSongs();
        List<SongGenre> songGenres = dto.getSongGenres();
        List<UpNextQueue> upNextQueues = dto.getUpNextQueues();
        
        
        Song updatedSong = songService.updateFullSong(
            id, song, editorialPlaylistSongs, listeningHistorys, playlistSongs, songGenres, upNextQueues
        );
        
        RestResponse<Song> response = RestResponse.buildSuccessResponse(
            HttpStatus.OK,
            "Song and its details updated successfully (Atomic operation)",
            updatedSong
        );
        return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
        RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.NOT_FOUND,
            String.format("Song not found with id: %s", id),
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
            String.format("An unexpected error occurred while updating the song: %s", ex.getMessage()),
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
      }
    }


	@Operation(
	    summary = "Delete song",

          description = "Delete an existing song and all its associated details. The operation is atomic: all or nothing."
      )
      @ApiResponses({
        @ApiResponse(
          responseCode = "200",
          description = "Song deleted successfully"
        ),
        @ApiResponse(
          responseCode = "404",
          description = "Song not found with the provided ID"
        ),
        @ApiResponse(
          responseCode = "500",
          description = "Internal server error"
        )
      })
      @DeleteMapping("/{id}")
      public ResponseEntity<RestResponse<Void>> deleteFullSong(
        @Parameter(description = "ID of the song to delete", required = true)
        @PathVariable Long id
      ) {
        try {
          songService.deleteFullSong(id);
          return ResponseEntity.ok(
            RestResponse.buildSuccessResponse(
              HttpStatus.OK,
              "Song deleted successfully",
              null
            )
          );
        } catch (ResourceNotFoundException ex) {
          RestResponse<Void> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.NOT_FOUND,
            String.format("Song not found with id: %s", id),
            null
          );
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
          RestResponse<Void> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            String.format("Error while performing atomic deletion of song: %s", ex.getMessage()),
            null
          );
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
      } 

@Operation(
  summary = "Get editorialPlaylistSongs attached with a song ID",
  description = "Retrieve a list of editorialPlaylistSongs from a song item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "EditorialPlaylistSongs retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "Song not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/editorialplaylistsongs")
public ResponseEntity<?> getEditorialPlaylistSongsBySong(
  @Parameter(description = "ID of the song to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  Song song = songService.getSongById(id);
  if (song == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("Song not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  EditorialPlaylistSongSearch object = new EditorialPlaylistSongSearch();
  object.setSongidSongs(song);
  
  Page<EditorialPlaylistSong> editorialPlaylistSongsData = editorialplaylistsongService.getAllEditorialPlaylistSong(pageable, object);
  
  RestResponse<Page<EditorialPlaylistSong>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "EditorialPlaylistSongs retrieved successfully",
    editorialPlaylistSongsData
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
    String.format("Error while retrieving song: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get listeningHistorys attached with a song ID",
  description = "Retrieve a list of listeningHistorys from a song item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "ListeningHistorys retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "Song not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/listeninghistorys")
public ResponseEntity<?> getListeningHistorysBySong(
  @Parameter(description = "ID of the song to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  Song song = songService.getSongById(id);
  if (song == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("Song not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  ListeningHistorySearch object = new ListeningHistorySearch();
  object.setSongidSongs(song);
  
  Page<ListeningHistory> listeningHistorysData = listeninghistoryService.getAllListeningHistory(pageable, object);
  
  RestResponse<Page<ListeningHistory>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "ListeningHistorys retrieved successfully",
    listeningHistorysData
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
    String.format("Error while retrieving song: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get playlistSongs attached with a song ID",
  description = "Retrieve a list of playlistSongs from a song item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "PlaylistSongs retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "Song not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/playlistsongs")
public ResponseEntity<?> getPlaylistSongsBySong(
  @Parameter(description = "ID of the song to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  Song song = songService.getSongById(id);
  if (song == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("Song not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  PlaylistSongSearch object = new PlaylistSongSearch();
  object.setSongidSongs(song);
  
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
    String.format("Error while retrieving song: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get songGenres attached with a song ID",
  description = "Retrieve a list of songGenres from a song item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "SongGenres retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "Song not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/songgenres")
public ResponseEntity<?> getSongGenresBySong(
  @Parameter(description = "ID of the song to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  Song song = songService.getSongById(id);
  if (song == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("Song not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  SongGenreSearch object = new SongGenreSearch();
  object.setSongidSongs(song);
  
  Page<SongGenre> songGenresData = songgenreService.getAllSongGenre(pageable, object);
  
  RestResponse<Page<SongGenre>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "SongGenres retrieved successfully",
    songGenresData
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
    String.format("Error while retrieving song: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get upNextQueues attached with a song ID",
  description = "Retrieve a list of upNextQueues from a song item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "UpNextQueues retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "Song not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/upnextqueues")
public ResponseEntity<?> getUpNextQueuesBySong(
  @Parameter(description = "ID of the song to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  Song song = songService.getSongById(id);
  if (song == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("Song not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  UpNextQueueSearch object = new UpNextQueueSearch();
  object.setSongidSongs(song);
  
  Page<UpNextQueue> upNextQueuesData = upnextqueueService.getAllUpNextQueue(pageable, object);
  
  RestResponse<Page<UpNextQueue>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "UpNextQueues retrieved successfully",
    upNextQueuesData
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
    String.format("Error while retrieving song: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}


}
