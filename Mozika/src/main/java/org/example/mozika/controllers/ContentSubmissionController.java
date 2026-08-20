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
import org.example.mozika.models.ContentSubmission;
import org.example.mozika.models.dto.ContentSubmissionSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.ContentSubmissionService;
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
@RequestMapping("/contentsubmissions")
@Tag(name = "Content submission", description = "Content submission Management APIs")
public class ContentSubmissionController  {
	private final ContentSubmissionService contentsubmissionService;

	public ContentSubmissionController(ContentSubmissionService contentsubmissionService) {
	   this.contentsubmissionService = contentsubmissionService;
	}

	@Operation(
	    summary = "Retrieve all content submission",
	    description = "Get a paginated and sorted list of content submission items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of content submission",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No content submission found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<ContentSubmission>>> getAllContentSubmissions(
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
	    Page<ContentSubmission> contentsubmissions = contentsubmissionService.getAllContentSubmission(pageable);
	
	    String message = "content submission retrieved successfully";
	
	    if(!contentsubmissions.hasContent()) {
	            message = "No content submission found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<ContentSubmission>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            contentsubmissions
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all content submission",
	    description = "Get a paginated and sorted list of content submission items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of content submission",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No content submission found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<ContentSubmission>>> getAllContentSubmissions(
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
	    @RequestBody ContentSubmissionSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<ContentSubmission> contentsubmissions = contentsubmissionService.getAllContentSubmission(pageable, object);
	
	    String message = "content submission retrieved successfully";
	
	    if(!contentsubmissions.hasContent()) {
	            message = "No content submission found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<ContentSubmission>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            contentsubmissions
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get content submission by ID",
	    description = "Retrieve a specific content submission item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the content submission",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Content submission not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<ContentSubmission>> getContentSubmissionById(
	    @Parameter(description = "ID of the content submission to retrieve", required = true)
	    @PathVariable Long id
	) {
	    ContentSubmission contentsubmission = contentsubmissionService.getContentSubmissionById(id);
	    RestResponse<ContentSubmission> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "content submission retrieved successfully", contentsubmission);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Content submission to CSV",
	     description = "Generate a CSV file from a provided list of Content submission objects."
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
	public ResponseEntity<byte[]> exportContentSubmissionToCsv(@RequestBody List<ContentSubmission> contentsubmission) {
	  
	  String csvContent = contentsubmissionService.exportContentSubmissionToCSV(contentsubmission);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new content submission",
 
	    description = "Create a new content submission item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Content submission created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid content submission supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createContentSubmission(
	    @Parameter(description = "Content submission object to be created", required = true)
	    @RequestBody @Valid ContentSubmission contentsubmission
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
	    ContentSubmission newContentSubmission = contentsubmissionService.createContentSubmission(contentsubmission);
	    RestResponse<ContentSubmission> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "content submission created successfully", newContentSubmission);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing content submission",

	    description = "Update an existing content submission item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Content submission updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Content submission not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid content submission supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateContentSubmission(
	    @Parameter(description = "ID of the content submission to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated content submission object", required = true)
	    @RequestBody @Valid ContentSubmission contentsubmission
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
	    ContentSubmission updateContentSubmission = contentsubmissionService.updateContentSubmission(id, contentsubmission);
	    RestResponse<ContentSubmission> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "content submission updated successfully", updateContentSubmission);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete content submission",

	    description = "Delete a content submission item by its ID. Returns success even if content submission was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Content submission deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteContentSubmissionById(
	    @Parameter(description = "ID of the content submission to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			contentsubmissionService.getContentSubmissionById(id);
			contentsubmissionService.deleteContentSubmission(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"content submission deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"content submission already deleted or does not exist",
					null
				)
			);
		}
	}



}
