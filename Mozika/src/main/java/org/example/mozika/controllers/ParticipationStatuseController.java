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
import org.example.mozika.models.ParticipationStatuse;
import org.example.mozika.models.dto.ParticipationStatuseSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.ParticipationStatuseService;
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
@RequestMapping("/participationstatuses")
@Tag(name = "Participation statuse", description = "Participation statuse Management APIs")
public class ParticipationStatuseController  {
	private final ParticipationStatuseService participationstatuseService;

	public ParticipationStatuseController(ParticipationStatuseService participationstatuseService) {
	   this.participationstatuseService = participationstatuseService;
	}

	@Operation(
	    summary = "Retrieve all participation statuse",
	    description = "Get a paginated and sorted list of participation statuse items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of participation statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No participation statuse found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<ParticipationStatuse>>> getAllParticipationStatuses(
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
	    Page<ParticipationStatuse> participationstatuses = participationstatuseService.getAllParticipationStatuse(pageable);
	
	    String message = "participation statuse retrieved successfully";
	
	    if(!participationstatuses.hasContent()) {
	            message = "No participation statuse found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<ParticipationStatuse>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            participationstatuses
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all participation statuse",
	    description = "Get a paginated and sorted list of participation statuse items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of participation statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No participation statuse found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<ParticipationStatuse>>> getAllParticipationStatuses(
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
	    @RequestBody ParticipationStatuseSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<ParticipationStatuse> participationstatuses = participationstatuseService.getAllParticipationStatuse(pageable, object);
	
	    String message = "participation statuse retrieved successfully";
	
	    if(!participationstatuses.hasContent()) {
	            message = "No participation statuse found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<ParticipationStatuse>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            participationstatuses
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get participation statuse by ID",
	    description = "Retrieve a specific participation statuse item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the participation statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Participation statuse not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<ParticipationStatuse>> getParticipationStatuseById(
	    @Parameter(description = "ID of the participation statuse to retrieve", required = true)
	    @PathVariable Long id
	) {
	    ParticipationStatuse participationstatuse = participationstatuseService.getParticipationStatuseById(id);
	    RestResponse<ParticipationStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "participation statuse retrieved successfully", participationstatuse);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Participation statuse to CSV",
	     description = "Generate a CSV file from a provided list of Participation statuse objects."
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
	public ResponseEntity<byte[]> exportParticipationStatuseToCsv(@RequestBody List<ParticipationStatuse> participationstatuse) {
	  
	  String csvContent = participationstatuseService.exportParticipationStatuseToCSV(participationstatuse);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new participation statuse",
 
	    description = "Create a new participation statuse item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Participation statuse created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid participation statuse supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createParticipationStatuse(
	    @Parameter(description = "Participation statuse object to be created", required = true)
	    @RequestBody @Valid ParticipationStatuse participationstatuse
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
	    ParticipationStatuse newParticipationStatuse = participationstatuseService.createParticipationStatuse(participationstatuse);
	    RestResponse<ParticipationStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "participation statuse created successfully", newParticipationStatuse);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing participation statuse",

	    description = "Update an existing participation statuse item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Participation statuse updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Participation statuse not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid participation statuse supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateParticipationStatuse(
	    @Parameter(description = "ID of the participation statuse to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated participation statuse object", required = true)
	    @RequestBody @Valid ParticipationStatuse participationstatuse
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
	    ParticipationStatuse updateParticipationStatuse = participationstatuseService.updateParticipationStatuse(id, participationstatuse);
	    RestResponse<ParticipationStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "participation statuse updated successfully", updateParticipationStatuse);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete participation statuse",

	    description = "Delete a participation statuse item by its ID. Returns success even if participation statuse was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Participation statuse deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteParticipationStatuseById(
	    @Parameter(description = "ID of the participation statuse to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			participationstatuseService.getParticipationStatuseById(id);
			participationstatuseService.deleteParticipationStatuse(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"participation statuse deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"participation statuse already deleted or does not exist",
					null
				)
			);
		}
	}



}
