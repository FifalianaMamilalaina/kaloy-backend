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
import org.example.mozika.models.NotificationPreference;
import org.example.mozika.models.dto.NotificationPreferenceSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.NotificationPreferenceService;
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
@RequestMapping("/notificationpreferences")
@Tag(name = "Notification preference", description = "Notification preference Management APIs")
public class NotificationPreferenceController  {
	private final NotificationPreferenceService notificationpreferenceService;

	public NotificationPreferenceController(NotificationPreferenceService notificationpreferenceService) {
	   this.notificationpreferenceService = notificationpreferenceService;
	}

	@Operation(
	    summary = "Retrieve all notification preference",
	    description = "Get a paginated and sorted list of notification preference items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of notification preference",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No notification preference found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<NotificationPreference>>> getAllNotificationPreferences(
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
	    Page<NotificationPreference> notificationpreferences = notificationpreferenceService.getAllNotificationPreference(pageable);
	
	    String message = "notification preference retrieved successfully";
	
	    if(!notificationpreferences.hasContent()) {
	            message = "No notification preference found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<NotificationPreference>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            notificationpreferences
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all notification preference",
	    description = "Get a paginated and sorted list of notification preference items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of notification preference",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No notification preference found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<NotificationPreference>>> getAllNotificationPreferences(
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
	    @RequestBody NotificationPreferenceSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<NotificationPreference> notificationpreferences = notificationpreferenceService.getAllNotificationPreference(pageable, object);
	
	    String message = "notification preference retrieved successfully";
	
	    if(!notificationpreferences.hasContent()) {
	            message = "No notification preference found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<NotificationPreference>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            notificationpreferences
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get notification preference by ID",
	    description = "Retrieve a specific notification preference item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the notification preference",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Notification preference not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<NotificationPreference>> getNotificationPreferenceById(
	    @Parameter(description = "ID of the notification preference to retrieve", required = true)
	    @PathVariable Long id
	) {
	    NotificationPreference notificationpreference = notificationpreferenceService.getNotificationPreferenceById(id);
	    RestResponse<NotificationPreference> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "notification preference retrieved successfully", notificationpreference);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Notification preference to CSV",
	     description = "Generate a CSV file from a provided list of Notification preference objects."
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
	public ResponseEntity<byte[]> exportNotificationPreferenceToCsv(@RequestBody List<NotificationPreference> notificationpreference) {
	  
	  String csvContent = notificationpreferenceService.exportNotificationPreferenceToCSV(notificationpreference);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new notification preference",
 
	    description = "Create a new notification preference item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Notification preference created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid notification preference supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createNotificationPreference(
	    @Parameter(description = "Notification preference object to be created", required = true)
	    @RequestBody @Valid NotificationPreference notificationpreference
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
	    NotificationPreference newNotificationPreference = notificationpreferenceService.createNotificationPreference(notificationpreference);
	    RestResponse<NotificationPreference> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "notification preference created successfully", newNotificationPreference);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing notification preference",

	    description = "Update an existing notification preference item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Notification preference updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Notification preference not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid notification preference supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateNotificationPreference(
	    @Parameter(description = "ID of the notification preference to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated notification preference object", required = true)
	    @RequestBody @Valid NotificationPreference notificationpreference
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
	    NotificationPreference updateNotificationPreference = notificationpreferenceService.updateNotificationPreference(id, notificationpreference);
	    RestResponse<NotificationPreference> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "notification preference updated successfully", updateNotificationPreference);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete notification preference",

	    description = "Delete a notification preference item by its ID. Returns success even if notification preference was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Notification preference deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteNotificationPreferenceById(
	    @Parameter(description = "ID of the notification preference to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			notificationpreferenceService.getNotificationPreferenceById(id);
			notificationpreferenceService.deleteNotificationPreference(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"notification preference deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"notification preference already deleted or does not exist",
					null
				)
			);
		}
	}



}
