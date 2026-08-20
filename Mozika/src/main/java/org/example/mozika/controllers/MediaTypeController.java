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
import org.example.mozika.models.MediaType;
import org.example.mozika.models.dto.MediaTypeSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.MediaTypeService;
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
@RequestMapping("/mediatypes")
@Tag(name = "Media type", description = "Media type Management APIs")
public class MediaTypeController  {
	private final MediaTypeService mediatypeService;

	public MediaTypeController(MediaTypeService mediatypeService) {
	   this.mediatypeService = mediatypeService;
	}

	@Operation(
	    summary = "Retrieve all media type",
	    description = "Get a paginated and sorted list of media type items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of media type",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No media type found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<MediaType>>> getAllMediaTypes(
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
	    Page<MediaType> mediatypes = mediatypeService.getAllMediaType(pageable);
	
	    String message = "media type retrieved successfully";
	
	    if(!mediatypes.hasContent()) {
	            message = "No media type found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<MediaType>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            mediatypes
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all media type",
	    description = "Get a paginated and sorted list of media type items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of media type",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No media type found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<MediaType>>> getAllMediaTypes(
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
	    @RequestBody MediaTypeSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<MediaType> mediatypes = mediatypeService.getAllMediaType(pageable, object);
	
	    String message = "media type retrieved successfully";
	
	    if(!mediatypes.hasContent()) {
	            message = "No media type found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<MediaType>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            mediatypes
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get media type by ID",
	    description = "Retrieve a specific media type item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the media type",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Media type not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<MediaType>> getMediaTypeById(
	    @Parameter(description = "ID of the media type to retrieve", required = true)
	    @PathVariable Long id
	) {
	    MediaType mediatype = mediatypeService.getMediaTypeById(id);
	    RestResponse<MediaType> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "media type retrieved successfully", mediatype);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Media type to CSV",
	     description = "Generate a CSV file from a provided list of Media type objects."
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
	public ResponseEntity<byte[]> exportMediaTypeToCsv(@RequestBody List<MediaType> mediatype) {
	  
	  String csvContent = mediatypeService.exportMediaTypeToCSV(mediatype);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new media type",
 
	    description = "Create a new media type item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Media type created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid media type supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createMediaType(
	    @Parameter(description = "Media type object to be created", required = true)
	    @RequestBody @Valid MediaType mediatype
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
	    MediaType newMediaType = mediatypeService.createMediaType(mediatype);
	    RestResponse<MediaType> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "media type created successfully", newMediaType);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing media type",

	    description = "Update an existing media type item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Media type updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Media type not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid media type supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateMediaType(
	    @Parameter(description = "ID of the media type to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated media type object", required = true)
	    @RequestBody @Valid MediaType mediatype
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
	    MediaType updateMediaType = mediatypeService.updateMediaType(id, mediatype);
	    RestResponse<MediaType> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "media type updated successfully", updateMediaType);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete media type",

	    description = "Delete a media type item by its ID. Returns success even if media type was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Media type deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteMediaTypeById(
	    @Parameter(description = "ID of the media type to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			mediatypeService.getMediaTypeById(id);
			mediatypeService.deleteMediaType(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"media type deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"media type already deleted or does not exist",
					null
				)
			);
		}
	}



}
