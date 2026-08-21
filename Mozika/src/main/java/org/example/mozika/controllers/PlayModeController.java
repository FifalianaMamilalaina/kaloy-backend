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
import org.example.mozika.models.PlayMode;
import org.example.mozika.models.dto.PlayModeSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.PlayModeService;
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
@RequestMapping("/playmodes")
@Tag(name = "Play mode", description = "Play mode Management APIs")
public class PlayModeController  {
	private final PlayModeService playmodeService;

	public PlayModeController(PlayModeService playmodeService) {
	   this.playmodeService = playmodeService;
	}

	@Operation(
	    summary = "Retrieve all play mode",
	    description = "Get a paginated and sorted list of play mode items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of play mode",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No play mode found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<PlayMode>>> getAllPlayModes(
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
	    Page<PlayMode> playmodes = playmodeService.getAllPlayMode(pageable);
	
	    String message = "play mode retrieved successfully";
	
	    if(!playmodes.hasContent()) {
	            message = "No play mode found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<PlayMode>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            playmodes
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all play mode",
	    description = "Get a paginated and sorted list of play mode items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of play mode",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No play mode found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<PlayMode>>> getAllPlayModes(
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
	    @RequestBody PlayModeSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<PlayMode> playmodes = playmodeService.getAllPlayMode(pageable, object);
	
	    String message = "play mode retrieved successfully";
	
	    if(!playmodes.hasContent()) {
	            message = "No play mode found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<PlayMode>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            playmodes
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get play mode by ID",
	    description = "Retrieve a specific play mode item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the play mode",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Play mode not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<PlayMode>> getPlayModeById(
	    @Parameter(description = "ID of the play mode to retrieve", required = true)
	    @PathVariable Long id
	) {
	    PlayMode playmode = playmodeService.getPlayModeById(id);
	    RestResponse<PlayMode> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "play mode retrieved successfully", playmode);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Play mode to CSV",
	     description = "Generate a CSV file from a provided list of Play mode objects."
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
	public ResponseEntity<byte[]> exportPlayModeToCsv(@RequestBody List<PlayMode> playmode) {
	  
	  String csvContent = playmodeService.exportPlayModeToCSV(playmode);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new play mode",
 
	    description = "Create a new play mode item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Play mode created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid play mode supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createPlayMode(
	    @Parameter(description = "Play mode object to be created", required = true)
	    @RequestBody @Valid PlayMode playmode
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
	    PlayMode newPlayMode = playmodeService.createPlayMode(playmode);
	    RestResponse<PlayMode> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "play mode created successfully", newPlayMode);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing play mode",

	    description = "Update an existing play mode item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Play mode updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Play mode not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid play mode supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updatePlayMode(
	    @Parameter(description = "ID of the play mode to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated play mode object", required = true)
	    @RequestBody @Valid PlayMode playmode
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
	    PlayMode updatePlayMode = playmodeService.updatePlayMode(id, playmode);
	    RestResponse<PlayMode> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "play mode updated successfully", updatePlayMode);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete play mode",

	    description = "Delete a play mode item by its ID. Returns success even if play mode was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Play mode deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deletePlayModeById(
	    @Parameter(description = "ID of the play mode to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			playmodeService.getPlayModeById(id);
			playmodeService.deletePlayMode(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"play mode deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"play mode already deleted or does not exist",
					null
				)
			);
		}
	}



}
