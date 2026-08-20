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
import org.example.mozika.models.VerificationChannel;
import org.example.mozika.models.dto.VerificationChannelSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.VerificationChannelService;
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
@RequestMapping("/verificationchannels")
@Tag(name = "Verification channel", description = "Verification channel Management APIs")
public class VerificationChannelController  {
	private final VerificationChannelService verificationchannelService;

	public VerificationChannelController(VerificationChannelService verificationchannelService) {
	   this.verificationchannelService = verificationchannelService;
	}

	@Operation(
	    summary = "Retrieve all verification channel",
	    description = "Get a paginated and sorted list of verification channel items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of verification channel",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No verification channel found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<VerificationChannel>>> getAllVerificationChannels(
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
	    Page<VerificationChannel> verificationchannels = verificationchannelService.getAllVerificationChannel(pageable);
	
	    String message = "verification channel retrieved successfully";
	
	    if(!verificationchannels.hasContent()) {
	            message = "No verification channel found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<VerificationChannel>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            verificationchannels
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all verification channel",
	    description = "Get a paginated and sorted list of verification channel items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of verification channel",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No verification channel found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<VerificationChannel>>> getAllVerificationChannels(
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
	    @RequestBody VerificationChannelSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<VerificationChannel> verificationchannels = verificationchannelService.getAllVerificationChannel(pageable, object);
	
	    String message = "verification channel retrieved successfully";
	
	    if(!verificationchannels.hasContent()) {
	            message = "No verification channel found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<VerificationChannel>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            verificationchannels
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get verification channel by ID",
	    description = "Retrieve a specific verification channel item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the verification channel",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Verification channel not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<VerificationChannel>> getVerificationChannelById(
	    @Parameter(description = "ID of the verification channel to retrieve", required = true)
	    @PathVariable Long id
	) {
	    VerificationChannel verificationchannel = verificationchannelService.getVerificationChannelById(id);
	    RestResponse<VerificationChannel> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "verification channel retrieved successfully", verificationchannel);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Verification channel to CSV",
	     description = "Generate a CSV file from a provided list of Verification channel objects."
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
	public ResponseEntity<byte[]> exportVerificationChannelToCsv(@RequestBody List<VerificationChannel> verificationchannel) {
	  
	  String csvContent = verificationchannelService.exportVerificationChannelToCSV(verificationchannel);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new verification channel",
 
	    description = "Create a new verification channel item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Verification channel created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid verification channel supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createVerificationChannel(
	    @Parameter(description = "Verification channel object to be created", required = true)
	    @RequestBody @Valid VerificationChannel verificationchannel
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
	    VerificationChannel newVerificationChannel = verificationchannelService.createVerificationChannel(verificationchannel);
	    RestResponse<VerificationChannel> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "verification channel created successfully", newVerificationChannel);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing verification channel",

	    description = "Update an existing verification channel item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Verification channel updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Verification channel not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid verification channel supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateVerificationChannel(
	    @Parameter(description = "ID of the verification channel to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated verification channel object", required = true)
	    @RequestBody @Valid VerificationChannel verificationchannel
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
	    VerificationChannel updateVerificationChannel = verificationchannelService.updateVerificationChannel(id, verificationchannel);
	    RestResponse<VerificationChannel> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "verification channel updated successfully", updateVerificationChannel);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete verification channel",

	    description = "Delete a verification channel item by its ID. Returns success even if verification channel was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Verification channel deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteVerificationChannelById(
	    @Parameter(description = "ID of the verification channel to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			verificationchannelService.getVerificationChannelById(id);
			verificationchannelService.deleteVerificationChannel(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"verification channel deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"verification channel already deleted or does not exist",
					null
				)
			);
		}
	}



}
