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
import org.example.mozika.models.EventModerationStatuse;
import org.example.mozika.models.dto.EventModerationStatuseSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.EventModerationStatuseService;
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
@RequestMapping("/eventmoderationstatuses")
@Tag(name = "Event moderation statuse", description = "Event moderation statuse Management APIs")
public class EventModerationStatuseController  {
	private final EventModerationStatuseService eventmoderationstatuseService;

	public EventModerationStatuseController(EventModerationStatuseService eventmoderationstatuseService) {
	   this.eventmoderationstatuseService = eventmoderationstatuseService;
	}

	@Operation(
	    summary = "Retrieve all event moderation statuse",
	    description = "Get a paginated and sorted list of event moderation statuse items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of event moderation statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No event moderation statuse found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<EventModerationStatuse>>> getAllEventModerationStatuses(
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
	    Page<EventModerationStatuse> eventmoderationstatuses = eventmoderationstatuseService.getAllEventModerationStatuse(pageable);
	
	    String message = "event moderation statuse retrieved successfully";
	
	    if(!eventmoderationstatuses.hasContent()) {
	            message = "No event moderation statuse found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<EventModerationStatuse>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            eventmoderationstatuses
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all event moderation statuse",
	    description = "Get a paginated and sorted list of event moderation statuse items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of event moderation statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No event moderation statuse found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<EventModerationStatuse>>> getAllEventModerationStatuses(
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
	    @RequestBody EventModerationStatuseSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<EventModerationStatuse> eventmoderationstatuses = eventmoderationstatuseService.getAllEventModerationStatuse(pageable, object);
	
	    String message = "event moderation statuse retrieved successfully";
	
	    if(!eventmoderationstatuses.hasContent()) {
	            message = "No event moderation statuse found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<EventModerationStatuse>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            eventmoderationstatuses
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get event moderation statuse by ID",
	    description = "Retrieve a specific event moderation statuse item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the event moderation statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Event moderation statuse not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<EventModerationStatuse>> getEventModerationStatuseById(
	    @Parameter(description = "ID of the event moderation statuse to retrieve", required = true)
	    @PathVariable Long id
	) {
	    EventModerationStatuse eventmoderationstatuse = eventmoderationstatuseService.getEventModerationStatuseById(id);
	    RestResponse<EventModerationStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "event moderation statuse retrieved successfully", eventmoderationstatuse);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Event moderation statuse to CSV",
	     description = "Generate a CSV file from a provided list of Event moderation statuse objects."
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
	public ResponseEntity<byte[]> exportEventModerationStatuseToCsv(@RequestBody List<EventModerationStatuse> eventmoderationstatuse) {
	  
	  String csvContent = eventmoderationstatuseService.exportEventModerationStatuseToCSV(eventmoderationstatuse);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new event moderation statuse",
 
	    description = "Create a new event moderation statuse item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Event moderation statuse created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid event moderation statuse supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createEventModerationStatuse(
	    @Parameter(description = "Event moderation statuse object to be created", required = true)
	    @RequestBody @Valid EventModerationStatuse eventmoderationstatuse
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
	    EventModerationStatuse newEventModerationStatuse = eventmoderationstatuseService.createEventModerationStatuse(eventmoderationstatuse);
	    RestResponse<EventModerationStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "event moderation statuse created successfully", newEventModerationStatuse);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing event moderation statuse",

	    description = "Update an existing event moderation statuse item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Event moderation statuse updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Event moderation statuse not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid event moderation statuse supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateEventModerationStatuse(
	    @Parameter(description = "ID of the event moderation statuse to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated event moderation statuse object", required = true)
	    @RequestBody @Valid EventModerationStatuse eventmoderationstatuse
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
	    EventModerationStatuse updateEventModerationStatuse = eventmoderationstatuseService.updateEventModerationStatuse(id, eventmoderationstatuse);
	    RestResponse<EventModerationStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "event moderation statuse updated successfully", updateEventModerationStatuse);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete event moderation statuse",

	    description = "Delete a event moderation statuse item by its ID. Returns success even if event moderation statuse was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Event moderation statuse deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteEventModerationStatuseById(
	    @Parameter(description = "ID of the event moderation statuse to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			eventmoderationstatuseService.getEventModerationStatuseById(id);
			eventmoderationstatuseService.deleteEventModerationStatuse(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"event moderation statuse deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"event moderation statuse already deleted or does not exist",
					null
				)
			);
		}
	}



}
