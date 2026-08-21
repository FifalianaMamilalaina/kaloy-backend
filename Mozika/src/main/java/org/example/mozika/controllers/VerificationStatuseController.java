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
import org.example.mozika.models.VerificationStatuse;
import org.example.mozika.models.dto.VerificationStatuseSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.VerificationStatuseService;
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
@RequestMapping("/verificationstatuses")
@Tag(name = "Verification statuse", description = "Verification statuse Management APIs")
public class VerificationStatuseController  {
	private final VerificationStatuseService verificationstatuseService;

	public VerificationStatuseController(VerificationStatuseService verificationstatuseService) {
	   this.verificationstatuseService = verificationstatuseService;
	}

	@Operation(
	    summary = "Retrieve all verification statuse",
	    description = "Get a paginated and sorted list of verification statuse items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of verification statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No verification statuse found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<VerificationStatuse>>> getAllVerificationStatuses(
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
	    Page<VerificationStatuse> verificationstatuses = verificationstatuseService.getAllVerificationStatuse(pageable);
	
	    String message = "verification statuse retrieved successfully";
	
	    if(!verificationstatuses.hasContent()) {
	            message = "No verification statuse found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<VerificationStatuse>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            verificationstatuses
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all verification statuse",
	    description = "Get a paginated and sorted list of verification statuse items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of verification statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No verification statuse found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<VerificationStatuse>>> getAllVerificationStatuses(
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
	    @RequestBody VerificationStatuseSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<VerificationStatuse> verificationstatuses = verificationstatuseService.getAllVerificationStatuse(pageable, object);
	
	    String message = "verification statuse retrieved successfully";
	
	    if(!verificationstatuses.hasContent()) {
	            message = "No verification statuse found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<VerificationStatuse>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            verificationstatuses
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get verification statuse by ID",
	    description = "Retrieve a specific verification statuse item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the verification statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Verification statuse not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<VerificationStatuse>> getVerificationStatuseById(
	    @Parameter(description = "ID of the verification statuse to retrieve", required = true)
	    @PathVariable Long id
	) {
	    VerificationStatuse verificationstatuse = verificationstatuseService.getVerificationStatuseById(id);
	    RestResponse<VerificationStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "verification statuse retrieved successfully", verificationstatuse);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Verification statuse to CSV",
	     description = "Generate a CSV file from a provided list of Verification statuse objects."
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
	public ResponseEntity<byte[]> exportVerificationStatuseToCsv(@RequestBody List<VerificationStatuse> verificationstatuse) {
	  
	  String csvContent = verificationstatuseService.exportVerificationStatuseToCSV(verificationstatuse);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new verification statuse",
 
	    description = "Create a new verification statuse item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Verification statuse created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid verification statuse supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createVerificationStatuse(
	    @Parameter(description = "Verification statuse object to be created", required = true)
	    @RequestBody @Valid VerificationStatuse verificationstatuse
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
	    VerificationStatuse newVerificationStatuse = verificationstatuseService.createVerificationStatuse(verificationstatuse);
	    RestResponse<VerificationStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "verification statuse created successfully", newVerificationStatuse);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing verification statuse",

	    description = "Update an existing verification statuse item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Verification statuse updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Verification statuse not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid verification statuse supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateVerificationStatuse(
	    @Parameter(description = "ID of the verification statuse to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated verification statuse object", required = true)
	    @RequestBody @Valid VerificationStatuse verificationstatuse
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
	    VerificationStatuse updateVerificationStatuse = verificationstatuseService.updateVerificationStatuse(id, verificationstatuse);
	    RestResponse<VerificationStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "verification statuse updated successfully", updateVerificationStatuse);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete verification statuse",

	    description = "Delete a verification statuse item by its ID. Returns success even if verification statuse was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Verification statuse deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteVerificationStatuseById(
	    @Parameter(description = "ID of the verification statuse to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			verificationstatuseService.getVerificationStatuseById(id);
			verificationstatuseService.deleteVerificationStatuse(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"verification statuse deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"verification statuse already deleted or does not exist",
					null
				)
			);
		}
	}



}
