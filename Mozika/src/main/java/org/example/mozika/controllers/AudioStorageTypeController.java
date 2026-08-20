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
import org.example.mozika.models.AudioStorageType;
import org.example.mozika.models.dto.AudioStorageTypeSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.AudioStorageTypeService;
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
@RequestMapping("/audiostoragetypes")
@Tag(name = "Audio storage type", description = "Audio storage type Management APIs")
public class AudioStorageTypeController  {
	private final AudioStorageTypeService audiostoragetypeService;

	public AudioStorageTypeController(AudioStorageTypeService audiostoragetypeService) {
	   this.audiostoragetypeService = audiostoragetypeService;
	}

	@Operation(
	    summary = "Retrieve all audio storage type",
	    description = "Get a paginated and sorted list of audio storage type items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of audio storage type",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No audio storage type found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<AudioStorageType>>> getAllAudioStorageTypes(
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
	    Page<AudioStorageType> audiostoragetypes = audiostoragetypeService.getAllAudioStorageType(pageable);
	
	    String message = "audio storage type retrieved successfully";
	
	    if(!audiostoragetypes.hasContent()) {
	            message = "No audio storage type found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<AudioStorageType>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            audiostoragetypes
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all audio storage type",
	    description = "Get a paginated and sorted list of audio storage type items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of audio storage type",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No audio storage type found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<AudioStorageType>>> getAllAudioStorageTypes(
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
	    @RequestBody AudioStorageTypeSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<AudioStorageType> audiostoragetypes = audiostoragetypeService.getAllAudioStorageType(pageable, object);
	
	    String message = "audio storage type retrieved successfully";
	
	    if(!audiostoragetypes.hasContent()) {
	            message = "No audio storage type found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<AudioStorageType>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            audiostoragetypes
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get audio storage type by ID",
	    description = "Retrieve a specific audio storage type item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the audio storage type",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Audio storage type not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<AudioStorageType>> getAudioStorageTypeById(
	    @Parameter(description = "ID of the audio storage type to retrieve", required = true)
	    @PathVariable Long id
	) {
	    AudioStorageType audiostoragetype = audiostoragetypeService.getAudioStorageTypeById(id);
	    RestResponse<AudioStorageType> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "audio storage type retrieved successfully", audiostoragetype);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Audio storage type to CSV",
	     description = "Generate a CSV file from a provided list of Audio storage type objects."
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
	public ResponseEntity<byte[]> exportAudioStorageTypeToCsv(@RequestBody List<AudioStorageType> audiostoragetype) {
	  
	  String csvContent = audiostoragetypeService.exportAudioStorageTypeToCSV(audiostoragetype);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new audio storage type",
 
	    description = "Create a new audio storage type item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Audio storage type created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid audio storage type supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createAudioStorageType(
	    @Parameter(description = "Audio storage type object to be created", required = true)
	    @RequestBody @Valid AudioStorageType audiostoragetype
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
	    AudioStorageType newAudioStorageType = audiostoragetypeService.createAudioStorageType(audiostoragetype);
	    RestResponse<AudioStorageType> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "audio storage type created successfully", newAudioStorageType);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing audio storage type",

	    description = "Update an existing audio storage type item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Audio storage type updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Audio storage type not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid audio storage type supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateAudioStorageType(
	    @Parameter(description = "ID of the audio storage type to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated audio storage type object", required = true)
	    @RequestBody @Valid AudioStorageType audiostoragetype
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
	    AudioStorageType updateAudioStorageType = audiostoragetypeService.updateAudioStorageType(id, audiostoragetype);
	    RestResponse<AudioStorageType> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "audio storage type updated successfully", updateAudioStorageType);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete audio storage type",

	    description = "Delete a audio storage type item by its ID. Returns success even if audio storage type was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Audio storage type deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteAudioStorageTypeById(
	    @Parameter(description = "ID of the audio storage type to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			audiostoragetypeService.getAudioStorageTypeById(id);
			audiostoragetypeService.deleteAudioStorageType(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"audio storage type deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"audio storage type already deleted or does not exist",
					null
				)
			);
		}
	}



}
