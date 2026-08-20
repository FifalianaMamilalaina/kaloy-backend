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
import org.example.mozika.models.UpNextQueue;
import org.example.mozika.models.dto.UpNextQueueSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.UpNextQueueService;
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
@RequestMapping("/upnextqueues")
@Tag(name = "Up next queue", description = "Up next queue Management APIs")
public class UpNextQueueController  {
	private final UpNextQueueService upnextqueueService;

	public UpNextQueueController(UpNextQueueService upnextqueueService) {
	   this.upnextqueueService = upnextqueueService;
	}

	@Operation(
	    summary = "Retrieve all up next queue",
	    description = "Get a paginated and sorted list of up next queue items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of up next queue",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No up next queue found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<UpNextQueue>>> getAllUpNextQueues(
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
	    Page<UpNextQueue> upnextqueues = upnextqueueService.getAllUpNextQueue(pageable);
	
	    String message = "up next queue retrieved successfully";
	
	    if(!upnextqueues.hasContent()) {
	            message = "No up next queue found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<UpNextQueue>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            upnextqueues
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all up next queue",
	    description = "Get a paginated and sorted list of up next queue items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of up next queue",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No up next queue found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<UpNextQueue>>> getAllUpNextQueues(
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
	    @RequestBody UpNextQueueSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<UpNextQueue> upnextqueues = upnextqueueService.getAllUpNextQueue(pageable, object);
	
	    String message = "up next queue retrieved successfully";
	
	    if(!upnextqueues.hasContent()) {
	            message = "No up next queue found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<UpNextQueue>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            upnextqueues
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get up next queue by ID",
	    description = "Retrieve a specific up next queue item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the up next queue",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Up next queue not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<UpNextQueue>> getUpNextQueueById(
	    @Parameter(description = "ID of the up next queue to retrieve", required = true)
	    @PathVariable Long id
	) {
	    UpNextQueue upnextqueue = upnextqueueService.getUpNextQueueById(id);
	    RestResponse<UpNextQueue> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "up next queue retrieved successfully", upnextqueue);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Up next queue to CSV",
	     description = "Generate a CSV file from a provided list of Up next queue objects."
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
	public ResponseEntity<byte[]> exportUpNextQueueToCsv(@RequestBody List<UpNextQueue> upnextqueue) {
	  
	  String csvContent = upnextqueueService.exportUpNextQueueToCSV(upnextqueue);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new up next queue",
 
	    description = "Create a new up next queue item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Up next queue created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid up next queue supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createUpNextQueue(
	    @Parameter(description = "Up next queue object to be created", required = true)
	    @RequestBody @Valid UpNextQueue upnextqueue
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
	    UpNextQueue newUpNextQueue = upnextqueueService.createUpNextQueue(upnextqueue);
	    RestResponse<UpNextQueue> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "up next queue created successfully", newUpNextQueue);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing up next queue",

	    description = "Update an existing up next queue item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Up next queue updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Up next queue not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid up next queue supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUpNextQueue(
	    @Parameter(description = "ID of the up next queue to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated up next queue object", required = true)
	    @RequestBody @Valid UpNextQueue upnextqueue
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
	    UpNextQueue updateUpNextQueue = upnextqueueService.updateUpNextQueue(id, upnextqueue);
	    RestResponse<UpNextQueue> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "up next queue updated successfully", updateUpNextQueue);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete up next queue",

	    description = "Delete a up next queue item by its ID. Returns success even if up next queue was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Up next queue deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteUpNextQueueById(
	    @Parameter(description = "ID of the up next queue to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			upnextqueueService.getUpNextQueueById(id);
			upnextqueueService.deleteUpNextQueue(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"up next queue deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"up next queue already deleted or does not exist",
					null
				)
			);
		}
	}



}
