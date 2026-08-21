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
import org.example.mozika.models.SubmissionStatuse;
import org.example.mozika.models.dto.SubmissionStatuseSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.SubmissionStatuseService;
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
@RequestMapping("/submissionstatuses")
@Tag(name = "Submission statuse", description = "Submission statuse Management APIs")
public class SubmissionStatuseController  {
	private final SubmissionStatuseService submissionstatuseService;

	public SubmissionStatuseController(SubmissionStatuseService submissionstatuseService) {
	   this.submissionstatuseService = submissionstatuseService;
	}

	@Operation(
	    summary = "Retrieve all submission statuse",
	    description = "Get a paginated and sorted list of submission statuse items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of submission statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No submission statuse found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<SubmissionStatuse>>> getAllSubmissionStatuses(
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
	    Page<SubmissionStatuse> submissionstatuses = submissionstatuseService.getAllSubmissionStatuse(pageable);
	
	    String message = "submission statuse retrieved successfully";
	
	    if(!submissionstatuses.hasContent()) {
	            message = "No submission statuse found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<SubmissionStatuse>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            submissionstatuses
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all submission statuse",
	    description = "Get a paginated and sorted list of submission statuse items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of submission statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No submission statuse found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<SubmissionStatuse>>> getAllSubmissionStatuses(
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
	    @RequestBody SubmissionStatuseSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<SubmissionStatuse> submissionstatuses = submissionstatuseService.getAllSubmissionStatuse(pageable, object);
	
	    String message = "submission statuse retrieved successfully";
	
	    if(!submissionstatuses.hasContent()) {
	            message = "No submission statuse found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<SubmissionStatuse>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            submissionstatuses
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get submission statuse by ID",
	    description = "Retrieve a specific submission statuse item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the submission statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Submission statuse not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<SubmissionStatuse>> getSubmissionStatuseById(
	    @Parameter(description = "ID of the submission statuse to retrieve", required = true)
	    @PathVariable Long id
	) {
	    SubmissionStatuse submissionstatuse = submissionstatuseService.getSubmissionStatuseById(id);
	    RestResponse<SubmissionStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "submission statuse retrieved successfully", submissionstatuse);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Submission statuse to CSV",
	     description = "Generate a CSV file from a provided list of Submission statuse objects."
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
	public ResponseEntity<byte[]> exportSubmissionStatuseToCsv(@RequestBody List<SubmissionStatuse> submissionstatuse) {
	  
	  String csvContent = submissionstatuseService.exportSubmissionStatuseToCSV(submissionstatuse);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new submission statuse",
 
	    description = "Create a new submission statuse item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Submission statuse created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid submission statuse supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createSubmissionStatuse(
	    @Parameter(description = "Submission statuse object to be created", required = true)
	    @RequestBody @Valid SubmissionStatuse submissionstatuse
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
	    SubmissionStatuse newSubmissionStatuse = submissionstatuseService.createSubmissionStatuse(submissionstatuse);
	    RestResponse<SubmissionStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "submission statuse created successfully", newSubmissionStatuse);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing submission statuse",

	    description = "Update an existing submission statuse item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Submission statuse updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Submission statuse not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid submission statuse supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateSubmissionStatuse(
	    @Parameter(description = "ID of the submission statuse to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated submission statuse object", required = true)
	    @RequestBody @Valid SubmissionStatuse submissionstatuse
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
	    SubmissionStatuse updateSubmissionStatuse = submissionstatuseService.updateSubmissionStatuse(id, submissionstatuse);
	    RestResponse<SubmissionStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "submission statuse updated successfully", updateSubmissionStatuse);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete submission statuse",

	    description = "Delete a submission statuse item by its ID. Returns success even if submission statuse was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Submission statuse deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteSubmissionStatuseById(
	    @Parameter(description = "ID of the submission statuse to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			submissionstatuseService.getSubmissionStatuseById(id);
			submissionstatuseService.deleteSubmissionStatuse(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"submission statuse deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"submission statuse already deleted or does not exist",
					null
				)
			);
		}
	}



}
