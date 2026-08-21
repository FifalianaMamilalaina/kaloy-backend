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
import org.example.mozika.models.ArtistType;
import org.example.mozika.models.dto.ArtistTypeSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.ArtistTypeService;
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
@RequestMapping("/artisttypes")
@Tag(name = "Artist type", description = "Artist type Management APIs")
public class ArtistTypeController  {
	private final ArtistTypeService artisttypeService;

	public ArtistTypeController(ArtistTypeService artisttypeService) {
	   this.artisttypeService = artisttypeService;
	}

	@Operation(
	    summary = "Retrieve all artist type",
	    description = "Get a paginated and sorted list of artist type items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of artist type",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No artist type found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<ArtistType>>> getAllArtistTypes(
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
	    Page<ArtistType> artisttypes = artisttypeService.getAllArtistType(pageable);
	
	    String message = "artist type retrieved successfully";
	
	    if(!artisttypes.hasContent()) {
	            message = "No artist type found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<ArtistType>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            artisttypes
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all artist type",
	    description = "Get a paginated and sorted list of artist type items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of artist type",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No artist type found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<ArtistType>>> getAllArtistTypes(
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
	    @RequestBody ArtistTypeSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<ArtistType> artisttypes = artisttypeService.getAllArtistType(pageable, object);
	
	    String message = "artist type retrieved successfully";
	
	    if(!artisttypes.hasContent()) {
	            message = "No artist type found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<ArtistType>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            artisttypes
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get artist type by ID",
	    description = "Retrieve a specific artist type item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the artist type",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Artist type not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<ArtistType>> getArtistTypeById(
	    @Parameter(description = "ID of the artist type to retrieve", required = true)
	    @PathVariable Long id
	) {
	    ArtistType artisttype = artisttypeService.getArtistTypeById(id);
	    RestResponse<ArtistType> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "artist type retrieved successfully", artisttype);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Artist type to CSV",
	     description = "Generate a CSV file from a provided list of Artist type objects."
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
	public ResponseEntity<byte[]> exportArtistTypeToCsv(@RequestBody List<ArtistType> artisttype) {
	  
	  String csvContent = artisttypeService.exportArtistTypeToCSV(artisttype);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new artist type",
 
	    description = "Create a new artist type item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Artist type created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid artist type supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createArtistType(
	    @Parameter(description = "Artist type object to be created", required = true)
	    @RequestBody @Valid ArtistType artisttype
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
	    ArtistType newArtistType = artisttypeService.createArtistType(artisttype);
	    RestResponse<ArtistType> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "artist type created successfully", newArtistType);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing artist type",

	    description = "Update an existing artist type item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Artist type updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Artist type not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid artist type supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateArtistType(
	    @Parameter(description = "ID of the artist type to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated artist type object", required = true)
	    @RequestBody @Valid ArtistType artisttype
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
	    ArtistType updateArtistType = artisttypeService.updateArtistType(id, artisttype);
	    RestResponse<ArtistType> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "artist type updated successfully", updateArtistType);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete artist type",

	    description = "Delete a artist type item by its ID. Returns success even if artist type was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Artist type deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteArtistTypeById(
	    @Parameter(description = "ID of the artist type to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			artisttypeService.getArtistTypeById(id);
			artisttypeService.deleteArtistType(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"artist type deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"artist type already deleted or does not exist",
					null
				)
			);
		}
	}



}
