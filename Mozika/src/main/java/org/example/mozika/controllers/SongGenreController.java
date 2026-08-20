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
import org.example.mozika.models.SongGenre;
import org.example.mozika.models.dto.SongGenreSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.SongGenreService;
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
@RequestMapping("/songgenres")
@Tag(name = "Song genre", description = "Song genre Management APIs")
public class SongGenreController  {
	private final SongGenreService songgenreService;

	public SongGenreController(SongGenreService songgenreService) {
	   this.songgenreService = songgenreService;
	}

	@Operation(
	    summary = "Retrieve all song genre",
	    description = "Get a paginated and sorted list of song genre items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of song genre",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No song genre found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<SongGenre>>> getAllSongGenres(
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
	    Page<SongGenre> songgenres = songgenreService.getAllSongGenre(pageable);
	
	    String message = "song genre retrieved successfully";
	
	    if(!songgenres.hasContent()) {
	            message = "No song genre found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<SongGenre>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            songgenres
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all song genre",
	    description = "Get a paginated and sorted list of song genre items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of song genre",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No song genre found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<SongGenre>>> getAllSongGenres(
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
	    @RequestBody SongGenreSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<SongGenre> songgenres = songgenreService.getAllSongGenre(pageable, object);
	
	    String message = "song genre retrieved successfully";
	
	    if(!songgenres.hasContent()) {
	            message = "No song genre found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<SongGenre>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            songgenres
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get song genre by ID",
	    description = "Retrieve a specific song genre item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the song genre",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Song genre not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<SongGenre>> getSongGenreById(
	    @Parameter(description = "ID of the song genre to retrieve", required = true)
	    @PathVariable Long id
	) {
	    SongGenre songgenre = songgenreService.getSongGenreById(id);
	    RestResponse<SongGenre> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "song genre retrieved successfully", songgenre);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Song genre to CSV",
	     description = "Generate a CSV file from a provided list of Song genre objects."
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
	public ResponseEntity<byte[]> exportSongGenreToCsv(@RequestBody List<SongGenre> songgenre) {
	  
	  String csvContent = songgenreService.exportSongGenreToCSV(songgenre);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new song genre",
 
	    description = "Create a new song genre item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Song genre created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid song genre supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createSongGenre(
	    @Parameter(description = "Song genre object to be created", required = true)
	    @RequestBody @Valid SongGenre songgenre
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
	    SongGenre newSongGenre = songgenreService.createSongGenre(songgenre);
	    RestResponse<SongGenre> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "song genre created successfully", newSongGenre);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing song genre",

	    description = "Update an existing song genre item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Song genre updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Song genre not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid song genre supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateSongGenre(
	    @Parameter(description = "ID of the song genre to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated song genre object", required = true)
	    @RequestBody @Valid SongGenre songgenre
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
	    SongGenre updateSongGenre = songgenreService.updateSongGenre(id, songgenre);
	    RestResponse<SongGenre> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "song genre updated successfully", updateSongGenre);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete song genre",

	    description = "Delete a song genre item by its ID. Returns success even if song genre was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Song genre deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteSongGenreById(
	    @Parameter(description = "ID of the song genre to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			songgenreService.getSongGenreById(id);
			songgenreService.deleteSongGenre(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"song genre deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"song genre already deleted or does not exist",
					null
				)
			);
		}
	}



}
