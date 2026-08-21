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
import org.example.mozika.models.EditorialPlaylistSong;
import org.example.mozika.models.dto.EditorialPlaylistSongSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.EditorialPlaylistSongService;
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
@RequestMapping("/editorialplaylistsongs")
@Tag(name = "Editorial playlist song", description = "Editorial playlist song Management APIs")
public class EditorialPlaylistSongController  {
	private final EditorialPlaylistSongService editorialplaylistsongService;

	public EditorialPlaylistSongController(EditorialPlaylistSongService editorialplaylistsongService) {
	   this.editorialplaylistsongService = editorialplaylistsongService;
	}

	@Operation(
	    summary = "Retrieve all editorial playlist song",
	    description = "Get a paginated and sorted list of editorial playlist song items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of editorial playlist song",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No editorial playlist song found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<EditorialPlaylistSong>>> getAllEditorialPlaylistSongs(
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
	    Page<EditorialPlaylistSong> editorialplaylistsongs = editorialplaylistsongService.getAllEditorialPlaylistSong(pageable);
	
	    String message = "editorial playlist song retrieved successfully";
	
	    if(!editorialplaylistsongs.hasContent()) {
	            message = "No editorial playlist song found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<EditorialPlaylistSong>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            editorialplaylistsongs
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all editorial playlist song",
	    description = "Get a paginated and sorted list of editorial playlist song items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of editorial playlist song",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No editorial playlist song found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<EditorialPlaylistSong>>> getAllEditorialPlaylistSongs(
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
	    @RequestBody EditorialPlaylistSongSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<EditorialPlaylistSong> editorialplaylistsongs = editorialplaylistsongService.getAllEditorialPlaylistSong(pageable, object);
	
	    String message = "editorial playlist song retrieved successfully";
	
	    if(!editorialplaylistsongs.hasContent()) {
	            message = "No editorial playlist song found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<EditorialPlaylistSong>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            editorialplaylistsongs
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get editorial playlist song by ID",
	    description = "Retrieve a specific editorial playlist song item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the editorial playlist song",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Editorial playlist song not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<EditorialPlaylistSong>> getEditorialPlaylistSongById(
	    @Parameter(description = "ID of the editorial playlist song to retrieve", required = true)
	    @PathVariable Long id
	) {
	    EditorialPlaylistSong editorialplaylistsong = editorialplaylistsongService.getEditorialPlaylistSongById(id);
	    RestResponse<EditorialPlaylistSong> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "editorial playlist song retrieved successfully", editorialplaylistsong);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Editorial playlist song to CSV",
	     description = "Generate a CSV file from a provided list of Editorial playlist song objects."
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
	public ResponseEntity<byte[]> exportEditorialPlaylistSongToCsv(@RequestBody List<EditorialPlaylistSong> editorialplaylistsong) {
	  
	  String csvContent = editorialplaylistsongService.exportEditorialPlaylistSongToCSV(editorialplaylistsong);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new editorial playlist song",
 
	    description = "Create a new editorial playlist song item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Editorial playlist song created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid editorial playlist song supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createEditorialPlaylistSong(
	    @Parameter(description = "Editorial playlist song object to be created", required = true)
	    @RequestBody @Valid EditorialPlaylistSong editorialplaylistsong
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
	    EditorialPlaylistSong newEditorialPlaylistSong = editorialplaylistsongService.createEditorialPlaylistSong(editorialplaylistsong);
	    RestResponse<EditorialPlaylistSong> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "editorial playlist song created successfully", newEditorialPlaylistSong);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing editorial playlist song",

	    description = "Update an existing editorial playlist song item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Editorial playlist song updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Editorial playlist song not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid editorial playlist song supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateEditorialPlaylistSong(
	    @Parameter(description = "ID of the editorial playlist song to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated editorial playlist song object", required = true)
	    @RequestBody @Valid EditorialPlaylistSong editorialplaylistsong
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
	    EditorialPlaylistSong updateEditorialPlaylistSong = editorialplaylistsongService.updateEditorialPlaylistSong(id, editorialplaylistsong);
	    RestResponse<EditorialPlaylistSong> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "editorial playlist song updated successfully", updateEditorialPlaylistSong);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete editorial playlist song",

	    description = "Delete a editorial playlist song item by its ID. Returns success even if editorial playlist song was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Editorial playlist song deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteEditorialPlaylistSongById(
	    @Parameter(description = "ID of the editorial playlist song to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			editorialplaylistsongService.getEditorialPlaylistSongById(id);
			editorialplaylistsongService.deleteEditorialPlaylistSong(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"editorial playlist song deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"editorial playlist song already deleted or does not exist",
					null
				)
			);
		}
	}



}
