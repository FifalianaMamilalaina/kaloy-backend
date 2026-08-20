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
import org.example.mozika.models.NotificationType;
import org.example.mozika.models.dto.NotificationTypeSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.NotificationTypeService;
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
@RequestMapping("/notificationtypes")
@Tag(name = "Notification type", description = "Notification type Management APIs")
public class NotificationTypeController  {
	private final NotificationTypeService notificationtypeService;

	public NotificationTypeController(NotificationTypeService notificationtypeService) {
	   this.notificationtypeService = notificationtypeService;
	}

	@Operation(
	    summary = "Retrieve all notification type",
	    description = "Get a paginated and sorted list of notification type items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of notification type",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No notification type found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<NotificationType>>> getAllNotificationTypes(
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
	    Page<NotificationType> notificationtypes = notificationtypeService.getAllNotificationType(pageable);
	
	    String message = "notification type retrieved successfully";
	
	    if(!notificationtypes.hasContent()) {
	            message = "No notification type found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<NotificationType>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            notificationtypes
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all notification type",
	    description = "Get a paginated and sorted list of notification type items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of notification type",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No notification type found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<NotificationType>>> getAllNotificationTypes(
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
	    @RequestBody NotificationTypeSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<NotificationType> notificationtypes = notificationtypeService.getAllNotificationType(pageable, object);
	
	    String message = "notification type retrieved successfully";
	
	    if(!notificationtypes.hasContent()) {
	            message = "No notification type found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<NotificationType>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            notificationtypes
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get notification type by ID",
	    description = "Retrieve a specific notification type item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the notification type",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Notification type not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<NotificationType>> getNotificationTypeById(
	    @Parameter(description = "ID of the notification type to retrieve", required = true)
	    @PathVariable Long id
	) {
	    NotificationType notificationtype = notificationtypeService.getNotificationTypeById(id);
	    RestResponse<NotificationType> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "notification type retrieved successfully", notificationtype);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Notification type to CSV",
	     description = "Generate a CSV file from a provided list of Notification type objects."
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
	public ResponseEntity<byte[]> exportNotificationTypeToCsv(@RequestBody List<NotificationType> notificationtype) {
	  
	  String csvContent = notificationtypeService.exportNotificationTypeToCSV(notificationtype);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new notification type",
 
	    description = "Create a new notification type item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Notification type created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid notification type supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createNotificationType(
	    @Parameter(description = "Notification type object to be created", required = true)
	    @RequestBody @Valid NotificationType notificationtype
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
	    NotificationType newNotificationType = notificationtypeService.createNotificationType(notificationtype);
	    RestResponse<NotificationType> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "notification type created successfully", newNotificationType);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing notification type",

	    description = "Update an existing notification type item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Notification type updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Notification type not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid notification type supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateNotificationType(
	    @Parameter(description = "ID of the notification type to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated notification type object", required = true)
	    @RequestBody @Valid NotificationType notificationtype
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
	    NotificationType updateNotificationType = notificationtypeService.updateNotificationType(id, notificationtype);
	    RestResponse<NotificationType> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "notification type updated successfully", updateNotificationType);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete notification type",

	    description = "Delete a notification type item by its ID. Returns success even if notification type was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Notification type deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteNotificationTypeById(
	    @Parameter(description = "ID of the notification type to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			notificationtypeService.getNotificationTypeById(id);
			notificationtypeService.deleteNotificationType(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"notification type deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"notification type already deleted or does not exist",
					null
				)
			);
		}
	}



}
