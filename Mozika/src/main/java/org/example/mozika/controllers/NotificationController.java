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
import org.example.mozika.models.Notification;
import org.example.mozika.models.dto.NotificationSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.NotificationService;
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
@RequestMapping("/notifications")
@Tag(name = "Notification", description = "Notification Management APIs")
public class NotificationController  {
	private final NotificationService notificationService;

	public NotificationController(NotificationService notificationService) {
	   this.notificationService = notificationService;
	}

	@Operation(
	    summary = "Retrieve all notification",
	    description = "Get a paginated and sorted list of notification items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of notification",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No notification found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<Notification>>> getAllNotifications(
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
	    Page<Notification> notifications = notificationService.getAllNotification(pageable);
	
	    String message = "notification retrieved successfully";
	
	    if(!notifications.hasContent()) {
	            message = "No notification found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Notification>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            notifications
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all notification",
	    description = "Get a paginated and sorted list of notification items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of notification",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No notification found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<Notification>>> getAllNotifications(
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
	    @RequestBody NotificationSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<Notification> notifications = notificationService.getAllNotification(pageable, object);
	
	    String message = "notification retrieved successfully";
	
	    if(!notifications.hasContent()) {
	            message = "No notification found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Notification>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            notifications
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get notification by ID",
	    description = "Retrieve a specific notification item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the notification",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Notification not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<Notification>> getNotificationById(
	    @Parameter(description = "ID of the notification to retrieve", required = true)
	    @PathVariable Long id
	) {
	    Notification notification = notificationService.getNotificationById(id);
	    RestResponse<Notification> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "notification retrieved successfully", notification);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Notification to CSV",
	     description = "Generate a CSV file from a provided list of Notification objects."
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
	public ResponseEntity<byte[]> exportNotificationToCsv(@RequestBody List<Notification> notification) {
	  
	  String csvContent = notificationService.exportNotificationToCSV(notification);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new notification",
 
	    description = "Create a new notification item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Notification created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid notification supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createNotification(
	    @Parameter(description = "Notification object to be created", required = true)
	    @RequestBody @Valid Notification notification
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
	    Notification newNotification = notificationService.createNotification(notification);
	    RestResponse<Notification> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "notification created successfully", newNotification);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing notification",

	    description = "Update an existing notification item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Notification updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Notification not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid notification supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateNotification(
	    @Parameter(description = "ID of the notification to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated notification object", required = true)
	    @RequestBody @Valid Notification notification
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
	    Notification updateNotification = notificationService.updateNotification(id, notification);
	    RestResponse<Notification> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "notification updated successfully", updateNotification);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete notification",

	    description = "Delete a notification item by its ID. Returns success even if notification was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Notification deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteNotificationById(
	    @Parameter(description = "ID of the notification to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			notificationService.getNotificationById(id);
			notificationService.deleteNotification(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"notification deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"notification already deleted or does not exist",
					null
				)
			);
		}
	}



}
