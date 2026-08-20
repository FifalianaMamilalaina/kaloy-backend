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
import org.example.mozika.models.EventMedia;
import org.example.mozika.models.dto.EventMediaSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.EventMediaService;
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
@RequestMapping("/eventmedias")
@Tag(name = "Event media", description = "Event media Management APIs")
public class EventMediaController  {
	private final EventMediaService eventmediaService;

	public EventMediaController(EventMediaService eventmediaService) {
	   this.eventmediaService = eventmediaService;
	}

	@Operation(
	    summary = "Retrieve all event media",
	    description = "Get a paginated and sorted list of event media items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of event media",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No event media found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<EventMedia>>> getAllEventMedias(
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
	    Page<EventMedia> eventmedias = eventmediaService.getAllEventMedia(pageable);
	
	    String message = "event media retrieved successfully";
	
	    if(!eventmedias.hasContent()) {
	            message = "No event media found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<EventMedia>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            eventmedias
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all event media",
	    description = "Get a paginated and sorted list of event media items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of event media",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No event media found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<EventMedia>>> getAllEventMedias(
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
	    @RequestBody EventMediaSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<EventMedia> eventmedias = eventmediaService.getAllEventMedia(pageable, object);
	
	    String message = "event media retrieved successfully";
	
	    if(!eventmedias.hasContent()) {
	            message = "No event media found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<EventMedia>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            eventmedias
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get event media by ID",
	    description = "Retrieve a specific event media item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the event media",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Event media not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<EventMedia>> getEventMediaById(
	    @Parameter(description = "ID of the event media to retrieve", required = true)
	    @PathVariable Long id
	) {
	    EventMedia eventmedia = eventmediaService.getEventMediaById(id);
	    RestResponse<EventMedia> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "event media retrieved successfully", eventmedia);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Event media to CSV",
	     description = "Generate a CSV file from a provided list of Event media objects."
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
	public ResponseEntity<byte[]> exportEventMediaToCsv(@RequestBody List<EventMedia> eventmedia) {
	  
	  String csvContent = eventmediaService.exportEventMediaToCSV(eventmedia);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new event media",
 
	    description = "Create a new event media item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Event media created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid event media supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createEventMedia(
	    @Parameter(description = "Event media object to be created", required = true)
	    @RequestBody @Valid EventMedia eventmedia
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
	    EventMedia newEventMedia = eventmediaService.createEventMedia(eventmedia);
	    RestResponse<EventMedia> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "event media created successfully", newEventMedia);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing event media",

	    description = "Update an existing event media item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Event media updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Event media not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid event media supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateEventMedia(
	    @Parameter(description = "ID of the event media to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated event media object", required = true)
	    @RequestBody @Valid EventMedia eventmedia
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
	    EventMedia updateEventMedia = eventmediaService.updateEventMedia(id, eventmedia);
	    RestResponse<EventMedia> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "event media updated successfully", updateEventMedia);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete event media",

	    description = "Delete a event media item by its ID. Returns success even if event media was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Event media deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteEventMediaById(
	    @Parameter(description = "ID of the event media to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			eventmediaService.getEventMediaById(id);
			eventmediaService.deleteEventMedia(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"event media deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"event media already deleted or does not exist",
					null
				)
			);
		}
	}



}
