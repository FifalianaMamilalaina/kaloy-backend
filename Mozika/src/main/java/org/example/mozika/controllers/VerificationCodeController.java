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
import org.example.mozika.models.VerificationCode;
import org.example.mozika.models.dto.VerificationCodeSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.VerificationCodeService;
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
@RequestMapping("/verificationcodes")
@Tag(name = "Verification code", description = "Verification code Management APIs")
public class VerificationCodeController  {
	private final VerificationCodeService verificationcodeService;

	public VerificationCodeController(VerificationCodeService verificationcodeService) {
	   this.verificationcodeService = verificationcodeService;
	}

	@Operation(
	    summary = "Retrieve all verification code",
	    description = "Get a paginated and sorted list of verification code items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of verification code",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No verification code found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<VerificationCode>>> getAllVerificationCodes(
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
	    Page<VerificationCode> verificationcodes = verificationcodeService.getAllVerificationCode(pageable);
	
	    String message = "verification code retrieved successfully";
	
	    if(!verificationcodes.hasContent()) {
	            message = "No verification code found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<VerificationCode>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            verificationcodes
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all verification code",
	    description = "Get a paginated and sorted list of verification code items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of verification code",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No verification code found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<VerificationCode>>> getAllVerificationCodes(
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
	    @RequestBody VerificationCodeSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<VerificationCode> verificationcodes = verificationcodeService.getAllVerificationCode(pageable, object);
	
	    String message = "verification code retrieved successfully";
	
	    if(!verificationcodes.hasContent()) {
	            message = "No verification code found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<VerificationCode>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            verificationcodes
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get verification code by ID",
	    description = "Retrieve a specific verification code item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the verification code",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Verification code not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<VerificationCode>> getVerificationCodeById(
	    @Parameter(description = "ID of the verification code to retrieve", required = true)
	    @PathVariable Long id
	) {
	    VerificationCode verificationcode = verificationcodeService.getVerificationCodeById(id);
	    RestResponse<VerificationCode> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "verification code retrieved successfully", verificationcode);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Verification code to CSV",
	     description = "Generate a CSV file from a provided list of Verification code objects."
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
	public ResponseEntity<byte[]> exportVerificationCodeToCsv(@RequestBody List<VerificationCode> verificationcode) {
	  
	  String csvContent = verificationcodeService.exportVerificationCodeToCSV(verificationcode);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new verification code",
 
	    description = "Create a new verification code item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Verification code created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid verification code supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createVerificationCode(
	    @Parameter(description = "Verification code object to be created", required = true)
	    @RequestBody @Valid VerificationCode verificationcode
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
	    VerificationCode newVerificationCode = verificationcodeService.createVerificationCode(verificationcode);
	    RestResponse<VerificationCode> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "verification code created successfully", newVerificationCode);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing verification code",

	    description = "Update an existing verification code item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Verification code updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Verification code not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid verification code supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateVerificationCode(
	    @Parameter(description = "ID of the verification code to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated verification code object", required = true)
	    @RequestBody @Valid VerificationCode verificationcode
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
	    VerificationCode updateVerificationCode = verificationcodeService.updateVerificationCode(id, verificationcode);
	    RestResponse<VerificationCode> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "verification code updated successfully", updateVerificationCode);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete verification code",

	    description = "Delete a verification code item by its ID. Returns success even if verification code was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Verification code deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteVerificationCodeById(
	    @Parameter(description = "ID of the verification code to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			verificationcodeService.getVerificationCodeById(id);
			verificationcodeService.deleteVerificationCode(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"verification code deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"verification code already deleted or does not exist",
					null
				)
			);
		}
	}



}
