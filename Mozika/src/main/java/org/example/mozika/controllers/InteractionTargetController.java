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
import org.example.mozika.models.InteractionTarget;
import org.example.mozika.models.dto.InteractionTargetSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.InteractionTargetService;
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
@RequestMapping("/interactiontargets")
@Tag(name = "Interaction target", description = "Interaction target Management APIs")
public class InteractionTargetController  {
	private final InteractionTargetService interactiontargetService;

	public InteractionTargetController(InteractionTargetService interactiontargetService) {
	   this.interactiontargetService = interactiontargetService;
	}

	@Operation(
	    summary = "Retrieve all interaction target",
	    description = "Get a paginated and sorted list of interaction target items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of interaction target",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No interaction target found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<InteractionTarget>>> getAllInteractionTargets(
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
	    Page<InteractionTarget> interactiontargets = interactiontargetService.getAllInteractionTarget(pageable);
	
	    String message = "interaction target retrieved successfully";
	
	    if(!interactiontargets.hasContent()) {
	            message = "No interaction target found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<InteractionTarget>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            interactiontargets
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all interaction target",
	    description = "Get a paginated and sorted list of interaction target items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of interaction target",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No interaction target found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<InteractionTarget>>> getAllInteractionTargets(
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
	    @RequestBody InteractionTargetSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<InteractionTarget> interactiontargets = interactiontargetService.getAllInteractionTarget(pageable, object);
	
	    String message = "interaction target retrieved successfully";
	
	    if(!interactiontargets.hasContent()) {
	            message = "No interaction target found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<InteractionTarget>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            interactiontargets
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get interaction target by ID",
	    description = "Retrieve a specific interaction target item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the interaction target",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Interaction target not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<InteractionTarget>> getInteractionTargetById(
	    @Parameter(description = "ID of the interaction target to retrieve", required = true)
	    @PathVariable Long id
	) {
	    InteractionTarget interactiontarget = interactiontargetService.getInteractionTargetById(id);
	    RestResponse<InteractionTarget> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "interaction target retrieved successfully", interactiontarget);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Interaction target to CSV",
	     description = "Generate a CSV file from a provided list of Interaction target objects."
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
	public ResponseEntity<byte[]> exportInteractionTargetToCsv(@RequestBody List<InteractionTarget> interactiontarget) {
	  
	  String csvContent = interactiontargetService.exportInteractionTargetToCSV(interactiontarget);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new interaction target",
 
	    description = "Create a new interaction target item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Interaction target created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid interaction target supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createInteractionTarget(
	    @Parameter(description = "Interaction target object to be created", required = true)
	    @RequestBody @Valid InteractionTarget interactiontarget
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
	    InteractionTarget newInteractionTarget = interactiontargetService.createInteractionTarget(interactiontarget);
	    RestResponse<InteractionTarget> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "interaction target created successfully", newInteractionTarget);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing interaction target",

	    description = "Update an existing interaction target item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Interaction target updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Interaction target not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid interaction target supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateInteractionTarget(
	    @Parameter(description = "ID of the interaction target to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated interaction target object", required = true)
	    @RequestBody @Valid InteractionTarget interactiontarget
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
	    InteractionTarget updateInteractionTarget = interactiontargetService.updateInteractionTarget(id, interactiontarget);
	    RestResponse<InteractionTarget> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "interaction target updated successfully", updateInteractionTarget);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete interaction target",

	    description = "Delete a interaction target item by its ID. Returns success even if interaction target was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Interaction target deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteInteractionTargetById(
	    @Parameter(description = "ID of the interaction target to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			interactiontargetService.getInteractionTargetById(id);
			interactiontargetService.deleteInteractionTarget(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"interaction target deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"interaction target already deleted or does not exist",
					null
				)
			);
		}
	}



}
