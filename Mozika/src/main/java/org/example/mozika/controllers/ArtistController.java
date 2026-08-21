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
import org.example.mozika.models.Artist;
import org.example.mozika.models.dto.ArtistSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.ArtistService;
import org.example.mozika.services.interfaces.AlbumService;
import org.example.mozika.models.dto.AlbumSearch;
import org.example.mozika.models.Album;
import org.example.mozika.services.interfaces.ArtistGroupMemberService;
import org.example.mozika.models.dto.ArtistGroupMemberSearch;
import org.example.mozika.models.ArtistGroupMember;
import org.example.mozika.services.interfaces.ConcertService;
import org.example.mozika.models.dto.ConcertSearch;
import org.example.mozika.models.Concert;
import org.example.mozika.services.interfaces.ContentSubmissionService;
import org.example.mozika.models.dto.ContentSubmissionSearch;
import org.example.mozika.models.ContentSubmission;
import org.example.mozika.services.interfaces.EditorialPlaylistService;
import org.example.mozika.models.dto.EditorialPlaylistSearch;
import org.example.mozika.models.EditorialPlaylist;
import org.example.mozika.services.interfaces.EventService;
import org.example.mozika.models.dto.EventSearch;
import org.example.mozika.models.Event;
import org.example.mozika.services.interfaces.FollowService;
import org.example.mozika.models.dto.FollowSearch;
import org.example.mozika.models.Follow;
import org.example.mozika.services.interfaces.SongService;
import org.example.mozika.models.dto.SongSearch;
import org.example.mozika.models.Song;


import org.example.mozika.models.dto.ArtistFullDto;

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
@RequestMapping("/artists")
@Tag(name = "Artist", description = "Artist Management APIs")
public class ArtistController  {
	private final ArtistService artistService;
private final AlbumService albumService;
private final ArtistGroupMemberService artistgroupmemberService;
private final ConcertService concertService;
private final ContentSubmissionService contentsubmissionService;
private final EditorialPlaylistService editorialplaylistService;
private final EventService eventService;
private final FollowService followService;
private final SongService songService;


	public ArtistController(ArtistService artistService, AlbumService albumService, ArtistGroupMemberService artistgroupmemberService, ConcertService concertService, ContentSubmissionService contentsubmissionService, EditorialPlaylistService editorialplaylistService, EventService eventService, FollowService followService, SongService songService) {
	   this.artistService = artistService;
	this.albumService = albumService;
	this.artistgroupmemberService = artistgroupmemberService;
	this.concertService = concertService;
	this.contentsubmissionService = contentsubmissionService;
	this.editorialplaylistService = editorialplaylistService;
	this.eventService = eventService;
	this.followService = followService;
	this.songService = songService;
	
	}

	@Operation(
	    summary = "Retrieve all artist",
	    description = "Get a paginated and sorted list of artist items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of artist",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No artist found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<Artist>>> getAllArtists(
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
	    Page<Artist> artists = artistService.getAllArtist(pageable);
	
	    String message = "artist retrieved successfully";
	
	    if(!artists.hasContent()) {
	            message = "No artist found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Artist>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            artists
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all artist",
	    description = "Get a paginated and sorted list of artist items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of artist",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No artist found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<Artist>>> getAllArtists(
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
	    @RequestBody ArtistSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<Artist> artists = artistService.getAllArtist(pageable, object);
	
	    String message = "artist retrieved successfully";
	
	    if(!artists.hasContent()) {
	            message = "No artist found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Artist>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            artists
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get artist by ID",
	    description = "Retrieve a specific artist item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the artist",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Artist not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<Artist>> getArtistById(
	    @Parameter(description = "ID of the artist to retrieve", required = true)
	    @PathVariable Long id
	) {
	    Artist artist = artistService.getArtistById(id);
	    RestResponse<Artist> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "artist retrieved successfully", artist);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Artist to CSV",
	     description = "Generate a CSV file from a provided list of Artist objects."
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
	public ResponseEntity<byte[]> exportArtistToCsv(@RequestBody List<Artist> artist) {
	  
	  String csvContent = artistService.exportArtistToCSV(artist);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new artist",

	    description = "Create a new artist and its details. The operation is atomic: all or nothing."
	)
	@ApiResponses({
	   @ApiResponse(
	     responseCode = "201",
	     description = "Artist created successfully",
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
	public ResponseEntity<?> createFullArtist(
	    @Parameter(description = "Artist object to be created with albums{{#unless @last}}, {{/unless}}artistGroupMembers{{#unless @last}}, {{/unless}}concerts{{#unless @last}}, {{/unless}}contentSubmissions{{#unless @last}}, {{/unless}}editorialPlaylists{{#unless @last}}, {{/unless}}events{{#unless @last}}, {{/unless}}follows{{#unless @last}}, {{/unless}}songs{{#unless @last}}, {{/unless}}", required = true)
	    @RequestBody @Valid ArtistFullDto dto,
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
	    Artist artist = dto.getArtist();
	    List<Album> albums = dto.getAlbums();
	    List<ArtistGroupMember> artistGroupMembers = dto.getArtistGroupMembers();
	    List<Concert> concerts = dto.getConcerts();
	    List<ContentSubmission> contentSubmissions = dto.getContentSubmissions();
	    List<EditorialPlaylist> editorialPlaylists = dto.getEditorialPlaylists();
	    List<Event> events = dto.getEvents();
	    List<Follow> follows = dto.getFollows();
	    List<Song> songs = dto.getSongs();
	    
	    Artist createdArtist = artistService.createFullArtist(artist, albums, artistGroupMembers, concerts, contentSubmissions, editorialPlaylists, events, follows, songs);
	    RestResponse<Artist> response = RestResponse.buildSuccessResponse(
	      HttpStatus.CREATED,
	      "Artist and its details created successfully (Atomic operation)",
	      createdArtist
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
	      String.format("An unexpected error occurred while creating the artist: %s", ex.getMessage()),
	      null
	    );
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	  }
	}

 
	@Operation(
	    summary = "Update existing artist",

          description = "Update an existing artist and its details. The operation is atomic: all or nothing."
      )
      @ApiResponses({
      @ApiResponse(
      responseCode = "200",
      description = "Artist updated successfully",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
      ),
      @ApiResponse(
      responseCode = "404",
      description = "Artist not found with the provided ID"
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
    public ResponseEntity<?> updateFullArtist(
      @Parameter(description = "ID of the artist to update", required = true)
      @PathVariable Long id,
      @Parameter(description = "Updated artist object with albums{{#unless @last}}, {{/unless}}artistGroupMembers{{#unless @last}}, {{/unless}}concerts{{#unless @last}}, {{/unless}}contentSubmissions{{#unless @last}}, {{/unless}}editorialPlaylists{{#unless @last}}, {{/unless}}events{{#unless @last}}, {{/unless}}follows{{#unless @last}}, {{/unless}}songs{{#unless @last}}, {{/unless}}", required = true)
      @RequestBody @Valid ArtistFullDto dto,
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
        // Check if artist exists
        Artist existingArtist = artistService.getArtistById(id);
        if (existingArtist == null) {
          RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
          HttpStatus.NOT_FOUND,
          String.format("Artist not found with id: %s", id),
          null
          );
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        
        Artist artist = dto.getArtist();
        List<Album> albums = dto.getAlbums();
        List<ArtistGroupMember> artistGroupMembers = dto.getArtistGroupMembers();
        List<Concert> concerts = dto.getConcerts();
        List<ContentSubmission> contentSubmissions = dto.getContentSubmissions();
        List<EditorialPlaylist> editorialPlaylists = dto.getEditorialPlaylists();
        List<Event> events = dto.getEvents();
        List<Follow> follows = dto.getFollows();
        List<Song> songs = dto.getSongs();
        
        
        Artist updatedArtist = artistService.updateFullArtist(
            id, artist, albums, artistGroupMembers, concerts, contentSubmissions, editorialPlaylists, events, follows, songs
        );
        
        RestResponse<Artist> response = RestResponse.buildSuccessResponse(
            HttpStatus.OK,
            "Artist and its details updated successfully (Atomic operation)",
            updatedArtist
        );
        return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
        RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.NOT_FOUND,
            String.format("Artist not found with id: %s", id),
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
            String.format("An unexpected error occurred while updating the artist: %s", ex.getMessage()),
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
      }
    }


	@Operation(
	    summary = "Delete artist",

          description = "Delete an existing artist and all its associated details. The operation is atomic: all or nothing."
      )
      @ApiResponses({
        @ApiResponse(
          responseCode = "200",
          description = "Artist deleted successfully"
        ),
        @ApiResponse(
          responseCode = "404",
          description = "Artist not found with the provided ID"
        ),
        @ApiResponse(
          responseCode = "500",
          description = "Internal server error"
        )
      })
      @DeleteMapping("/{id}")
      public ResponseEntity<RestResponse<Void>> deleteFullArtist(
        @Parameter(description = "ID of the artist to delete", required = true)
        @PathVariable Long id
      ) {
        try {
          artistService.deleteFullArtist(id);
          return ResponseEntity.ok(
            RestResponse.buildSuccessResponse(
              HttpStatus.OK,
              "Artist deleted successfully",
              null
            )
          );
        } catch (ResourceNotFoundException ex) {
          RestResponse<Void> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.NOT_FOUND,
            String.format("Artist not found with id: %s", id),
            null
          );
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
          RestResponse<Void> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            String.format("Error while performing atomic deletion of artist: %s", ex.getMessage()),
            null
          );
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
      } 

@Operation(
  summary = "Get albums attached with a artist ID",
  description = "Retrieve a list of albums from a artist item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "Albums retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "Artist not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/albums")
public ResponseEntity<?> getAlbumsByArtist(
  @Parameter(description = "ID of the artist to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  Artist artist = artistService.getArtistById(id);
  if (artist == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("Artist not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  AlbumSearch object = new AlbumSearch();
  object.setArtistidArtists(artist);
  
  Page<Album> albumsData = albumService.getAllAlbum(pageable, object);
  
  RestResponse<Page<Album>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "Albums retrieved successfully",
    albumsData
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
    String.format("Error while retrieving artist: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get artistGroupMembers attached with a artist ID",
  description = "Retrieve a list of artistGroupMembers from a artist item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "ArtistGroupMembers retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "Artist not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/artistgroupmembers")
public ResponseEntity<?> getArtistGroupMembersByArtist(
  @Parameter(description = "ID of the artist to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  Artist artist = artistService.getArtistById(id);
  if (artist == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("Artist not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  ArtistGroupMemberSearch object = new ArtistGroupMemberSearch();
  object.setGroupartistidArtists(artist);
  
  Page<ArtistGroupMember> artistGroupMembersData = artistgroupmemberService.getAllArtistGroupMember(pageable, object);
  
  RestResponse<Page<ArtistGroupMember>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "ArtistGroupMembers retrieved successfully",
    artistGroupMembersData
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
    String.format("Error while retrieving artist: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get concerts attached with a artist ID",
  description = "Retrieve a list of concerts from a artist item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "Concerts retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "Artist not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/concerts")
public ResponseEntity<?> getConcertsByArtist(
  @Parameter(description = "ID of the artist to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  Artist artist = artistService.getArtistById(id);
  if (artist == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("Artist not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  ConcertSearch object = new ConcertSearch();
  object.setArtistidArtists(artist);
  
  Page<Concert> concertsData = concertService.getAllConcert(pageable, object);
  
  RestResponse<Page<Concert>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "Concerts retrieved successfully",
    concertsData
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
    String.format("Error while retrieving artist: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get contentSubmissions attached with a artist ID",
  description = "Retrieve a list of contentSubmissions from a artist item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "ContentSubmissions retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "Artist not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/contentsubmissions")
public ResponseEntity<?> getContentSubmissionsByArtist(
  @Parameter(description = "ID of the artist to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  Artist artist = artistService.getArtistById(id);
  if (artist == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("Artist not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  ContentSubmissionSearch object = new ContentSubmissionSearch();
  object.setArtistidArtists(artist);
  
  Page<ContentSubmission> contentSubmissionsData = contentsubmissionService.getAllContentSubmission(pageable, object);
  
  RestResponse<Page<ContentSubmission>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "ContentSubmissions retrieved successfully",
    contentSubmissionsData
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
    String.format("Error while retrieving artist: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get editorialPlaylists attached with a artist ID",
  description = "Retrieve a list of editorialPlaylists from a artist item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "EditorialPlaylists retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "Artist not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/editorialplaylists")
public ResponseEntity<?> getEditorialPlaylistsByArtist(
  @Parameter(description = "ID of the artist to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  Artist artist = artistService.getArtistById(id);
  if (artist == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("Artist not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  EditorialPlaylistSearch object = new EditorialPlaylistSearch();
  object.setArtistidArtists(artist);
  
  Page<EditorialPlaylist> editorialPlaylistsData = editorialplaylistService.getAllEditorialPlaylist(pageable, object);
  
  RestResponse<Page<EditorialPlaylist>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "EditorialPlaylists retrieved successfully",
    editorialPlaylistsData
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
    String.format("Error while retrieving artist: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get events attached with a artist ID",
  description = "Retrieve a list of events from a artist item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "Events retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "Artist not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/events")
public ResponseEntity<?> getEventsByArtist(
  @Parameter(description = "ID of the artist to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  Artist artist = artistService.getArtistById(id);
  if (artist == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("Artist not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  EventSearch object = new EventSearch();
  object.setCreatedbyartistidArtists(artist);
  
  Page<Event> eventsData = eventService.getAllEvent(pageable, object);
  
  RestResponse<Page<Event>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "Events retrieved successfully",
    eventsData
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
    String.format("Error while retrieving artist: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get follows attached with a artist ID",
  description = "Retrieve a list of follows from a artist item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "Follows retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "Artist not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/follows")
public ResponseEntity<?> getFollowsByArtist(
  @Parameter(description = "ID of the artist to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  Artist artist = artistService.getArtistById(id);
  if (artist == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("Artist not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  FollowSearch object = new FollowSearch();
  object.setArtistidArtists(artist);
  
  Page<Follow> followsData = followService.getAllFollow(pageable, object);
  
  RestResponse<Page<Follow>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "Follows retrieved successfully",
    followsData
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
    String.format("Error while retrieving artist: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get songs attached with a artist ID",
  description = "Retrieve a list of songs from a artist item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "Songs retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "Artist not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/songs")
public ResponseEntity<?> getSongsByArtist(
  @Parameter(description = "ID of the artist to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  Artist artist = artistService.getArtistById(id);
  if (artist == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("Artist not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  SongSearch object = new SongSearch();
  object.setArtistidArtists(artist);
  
  Page<Song> songsData = songService.getAllSong(pageable, object);
  
  RestResponse<Page<Song>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "Songs retrieved successfully",
    songsData
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
    String.format("Error while retrieving artist: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}


}
