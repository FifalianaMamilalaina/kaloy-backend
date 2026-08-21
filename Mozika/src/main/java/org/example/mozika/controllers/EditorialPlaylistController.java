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
import org.example.mozika.models.EditorialPlaylist;
import org.example.mozika.models.dto.EditorialPlaylistSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.EditorialPlaylistService;
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
@RequestMapping("/editorialplaylists")
@Tag(name = "Editorial playlist", description = "Editorial playlist Management APIs")
public class EditorialPlaylistController  {
	private final EditorialPlaylistService editorialplaylistService;

	public EditorialPlaylistController(EditorialPlaylistService editorialplaylistService) {
	   this.editorialplaylistService = editorialplaylistService;
	}

	@Operation(
	    summary = "Retrieve all editorial playlist",
	    description = "Get a paginated and sorted list of editorial playlist items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of editorial playlist",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No editorial playlist found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<EditorialPlaylist>>> getAllEditorialPlaylists(
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
	    Page<EditorialPlaylist> editorialplaylists = editorialplaylistService.getAllEditorialPlaylist(pageable);
	
	    String message = "editorial playlist retrieved successfully";
	
	    if(!editorialplaylists.hasContent()) {
	            message = "No editorial playlist found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<EditorialPlaylist>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            editorialplaylists
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all editorial playlist",
	    description = "Get a paginated and sorted list of editorial playlist items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of editorial playlist",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No editorial playlist found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<EditorialPlaylist>>> getAllEditorialPlaylists(
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
	    @RequestBody EditorialPlaylistSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<EditorialPlaylist> editorialplaylists = editorialplaylistService.getAllEditorialPlaylist(pageable, object);
	
	    String message = "editorial playlist retrieved successfully";
	
	    if(!editorialplaylists.hasContent()) {
	            message = "No editorial playlist found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<EditorialPlaylist>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            editorialplaylists
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get editorial playlist by ID",
	    description = "Retrieve a specific editorial playlist item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the editorial playlist",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Editorial playlist not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<EditorialPlaylist>> getEditorialPlaylistById(
	    @Parameter(description = "ID of the editorial playlist to retrieve", required = true)
	    @PathVariable Long id
	) {
	    EditorialPlaylist editorialplaylist = editorialplaylistService.getEditorialPlaylistById(id);
	    RestResponse<EditorialPlaylist> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "editorial playlist retrieved successfully", editorialplaylist);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Editorial playlist to CSV",
	     description = "Generate a CSV file from a provided list of Editorial playlist objects."
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
	public ResponseEntity<byte[]> exportEditorialPlaylistToCsv(@RequestBody List<EditorialPlaylist> editorialplaylist) {
	  
	  String csvContent = editorialplaylistService.exportEditorialPlaylistToCSV(editorialplaylist);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new editorial playlist",
 
	    description = "Create a new editorial playlist item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Editorial playlist created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid editorial playlist supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createEditorialPlaylist(
	    @Parameter(description = "Editorial playlist object to be created", required = true)
	    @RequestBody @Valid EditorialPlaylist editorialplaylist
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
	    EditorialPlaylist newEditorialPlaylist = editorialplaylistService.createEditorialPlaylist(editorialplaylist);
	    RestResponse<EditorialPlaylist> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "editorial playlist created successfully", newEditorialPlaylist);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing editorial playlist",

	    description = "Update an existing editorial playlist item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Editorial playlist updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Editorial playlist not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid editorial playlist supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateEditorialPlaylist(
	    @Parameter(description = "ID of the editorial playlist to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated editorial playlist object", required = true)
	    @RequestBody @Valid EditorialPlaylist editorialplaylist
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
	    EditorialPlaylist updateEditorialPlaylist = editorialplaylistService.updateEditorialPlaylist(id, editorialplaylist);
	    RestResponse<EditorialPlaylist> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "editorial playlist updated successfully", updateEditorialPlaylist);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete editorial playlist",

	    description = "Delete a editorial playlist item by its ID. Returns success even if editorial playlist was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Editorial playlist deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteEditorialPlaylistById(
	    @Parameter(description = "ID of the editorial playlist to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			editorialplaylistService.getEditorialPlaylistById(id);
			editorialplaylistService.deleteEditorialPlaylist(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"editorial playlist deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"editorial playlist already deleted or does not exist",
					null
				)
			);
		}
	}



}
