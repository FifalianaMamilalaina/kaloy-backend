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
import org.example.mozika.models.PlaylistSong;
import org.example.mozika.models.dto.PlaylistSongSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.PlaylistSongService;
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
@RequestMapping("/playlistsongs")
@Tag(name = "Playlist song", description = "Playlist song Management APIs")
public class PlaylistSongController  {
	private final PlaylistSongService playlistsongService;

	public PlaylistSongController(PlaylistSongService playlistsongService) {
	   this.playlistsongService = playlistsongService;
	}

	@Operation(
	    summary = "Retrieve all playlist song",
	    description = "Get a paginated and sorted list of playlist song items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of playlist song",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No playlist song found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<PlaylistSong>>> getAllPlaylistSongs(
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
	    Page<PlaylistSong> playlistsongs = playlistsongService.getAllPlaylistSong(pageable);
	
	    String message = "playlist song retrieved successfully";
	
	    if(!playlistsongs.hasContent()) {
	            message = "No playlist song found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<PlaylistSong>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            playlistsongs
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all playlist song",
	    description = "Get a paginated and sorted list of playlist song items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of playlist song",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No playlist song found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<PlaylistSong>>> getAllPlaylistSongs(
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
	    @RequestBody PlaylistSongSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<PlaylistSong> playlistsongs = playlistsongService.getAllPlaylistSong(pageable, object);
	
	    String message = "playlist song retrieved successfully";
	
	    if(!playlistsongs.hasContent()) {
	            message = "No playlist song found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<PlaylistSong>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            playlistsongs
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get playlist song by ID",
	    description = "Retrieve a specific playlist song item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the playlist song",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Playlist song not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<PlaylistSong>> getPlaylistSongById(
	    @Parameter(description = "ID of the playlist song to retrieve", required = true)
	    @PathVariable Long id
	) {
	    PlaylistSong playlistsong = playlistsongService.getPlaylistSongById(id);
	    RestResponse<PlaylistSong> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "playlist song retrieved successfully", playlistsong);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Playlist song to CSV",
	     description = "Generate a CSV file from a provided list of Playlist song objects."
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
	public ResponseEntity<byte[]> exportPlaylistSongToCsv(@RequestBody List<PlaylistSong> playlistsong) {
	  
	  String csvContent = playlistsongService.exportPlaylistSongToCSV(playlistsong);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new playlist song",
 
	    description = "Create a new playlist song item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Playlist song created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid playlist song supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createPlaylistSong(
	    @Parameter(description = "Playlist song object to be created", required = true)
	    @RequestBody @Valid PlaylistSong playlistsong
	    ,BindingResult bindingResult
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
	    PlaylistSong newPlaylistSong = playlistsongService.createPlaylistSong(playlistsong);
	    RestResponse<PlaylistSong> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "playlist song created successfully", newPlaylistSong);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing playlist song",

	    description = "Update an existing playlist song item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Playlist song updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Playlist song not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid playlist song supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updatePlaylistSong(
	    @Parameter(description = "ID of the playlist song to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated playlist song object", required = true)
	    @RequestBody @Valid PlaylistSong playlistsong
	    ,BindingResult bindingResult
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
	    PlaylistSong updatePlaylistSong = playlistsongService.updatePlaylistSong(id, playlistsong);
	    RestResponse<PlaylistSong> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "playlist song updated successfully", updatePlaylistSong);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete playlist song",

	    description = "Delete a playlist song item by its ID. Returns success even if playlist song was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Playlist song deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deletePlaylistSongById(
	    @Parameter(description = "ID of the playlist song to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			playlistsongService.getPlaylistSongById(id);
			playlistsongService.deletePlaylistSong(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"playlist song deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"playlist song already deleted or does not exist",
					null
				)
			);
		}
	}



}
