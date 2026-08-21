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
import org.example.mozika.models.PlaylistVisibilitie;
import org.example.mozika.models.dto.PlaylistVisibilitieSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.PlaylistVisibilitieService;
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
@RequestMapping("/playlistvisibilities")
@Tag(name = "Playlist visibilitie", description = "Playlist visibilitie Management APIs")
public class PlaylistVisibilitieController  {
	private final PlaylistVisibilitieService playlistvisibilitieService;

	public PlaylistVisibilitieController(PlaylistVisibilitieService playlistvisibilitieService) {
	   this.playlistvisibilitieService = playlistvisibilitieService;
	}

	@Operation(
	    summary = "Retrieve all playlist visibilitie",
	    description = "Get a paginated and sorted list of playlist visibilitie items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of playlist visibilitie",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No playlist visibilitie found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<PlaylistVisibilitie>>> getAllPlaylistVisibilities(
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
	    Page<PlaylistVisibilitie> playlistvisibilities = playlistvisibilitieService.getAllPlaylistVisibilitie(pageable);
	
	    String message = "playlist visibilitie retrieved successfully";
	
	    if(!playlistvisibilities.hasContent()) {
	            message = "No playlist visibilitie found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<PlaylistVisibilitie>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            playlistvisibilities
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all playlist visibilitie",
	    description = "Get a paginated and sorted list of playlist visibilitie items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of playlist visibilitie",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No playlist visibilitie found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<PlaylistVisibilitie>>> getAllPlaylistVisibilities(
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
	    @RequestBody PlaylistVisibilitieSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<PlaylistVisibilitie> playlistvisibilities = playlistvisibilitieService.getAllPlaylistVisibilitie(pageable, object);
	
	    String message = "playlist visibilitie retrieved successfully";
	
	    if(!playlistvisibilities.hasContent()) {
	            message = "No playlist visibilitie found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<PlaylistVisibilitie>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            playlistvisibilities
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get playlist visibilitie by ID",
	    description = "Retrieve a specific playlist visibilitie item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the playlist visibilitie",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Playlist visibilitie not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<PlaylistVisibilitie>> getPlaylistVisibilitieById(
	    @Parameter(description = "ID of the playlist visibilitie to retrieve", required = true)
	    @PathVariable Long id
	) {
	    PlaylistVisibilitie playlistvisibilitie = playlistvisibilitieService.getPlaylistVisibilitieById(id);
	    RestResponse<PlaylistVisibilitie> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "playlist visibilitie retrieved successfully", playlistvisibilitie);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Playlist visibilitie to CSV",
	     description = "Generate a CSV file from a provided list of Playlist visibilitie objects."
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
	public ResponseEntity<byte[]> exportPlaylistVisibilitieToCsv(@RequestBody List<PlaylistVisibilitie> playlistvisibilitie) {
	  
	  String csvContent = playlistvisibilitieService.exportPlaylistVisibilitieToCSV(playlistvisibilitie);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new playlist visibilitie",
 
	    description = "Create a new playlist visibilitie item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Playlist visibilitie created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid playlist visibilitie supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createPlaylistVisibilitie(
	    @Parameter(description = "Playlist visibilitie object to be created", required = true)
	    @RequestBody @Valid PlaylistVisibilitie playlistvisibilitie
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
	    PlaylistVisibilitie newPlaylistVisibilitie = playlistvisibilitieService.createPlaylistVisibilitie(playlistvisibilitie);
	    RestResponse<PlaylistVisibilitie> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "playlist visibilitie created successfully", newPlaylistVisibilitie);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing playlist visibilitie",

	    description = "Update an existing playlist visibilitie item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Playlist visibilitie updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Playlist visibilitie not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid playlist visibilitie supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updatePlaylistVisibilitie(
	    @Parameter(description = "ID of the playlist visibilitie to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated playlist visibilitie object", required = true)
	    @RequestBody @Valid PlaylistVisibilitie playlistvisibilitie
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
	    PlaylistVisibilitie updatePlaylistVisibilitie = playlistvisibilitieService.updatePlaylistVisibilitie(id, playlistvisibilitie);
	    RestResponse<PlaylistVisibilitie> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "playlist visibilitie updated successfully", updatePlaylistVisibilitie);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete playlist visibilitie",

	    description = "Delete a playlist visibilitie item by its ID. Returns success even if playlist visibilitie was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Playlist visibilitie deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deletePlaylistVisibilitieById(
	    @Parameter(description = "ID of the playlist visibilitie to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			playlistvisibilitieService.getPlaylistVisibilitieById(id);
			playlistvisibilitieService.deletePlaylistVisibilitie(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"playlist visibilitie deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"playlist visibilitie already deleted or does not exist",
					null
				)
			);
		}
	}



}
