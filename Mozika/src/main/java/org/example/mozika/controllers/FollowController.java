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
import org.example.mozika.models.Follow;
import org.example.mozika.models.dto.FollowSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.FollowService;
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
@RequestMapping("/follows")
@Tag(name = "Follow", description = "Follow Management APIs")
public class FollowController  {
	private final FollowService followService;

	public FollowController(FollowService followService) {
	   this.followService = followService;
	}

	@Operation(
	    summary = "Retrieve all follow",
	    description = "Get a paginated and sorted list of follow items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of follow",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No follow found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<Follow>>> getAllFollows(
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
	    Page<Follow> follows = followService.getAllFollow(pageable);
	
	    String message = "follow retrieved successfully";
	
	    if(!follows.hasContent()) {
	            message = "No follow found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Follow>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            follows
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all follow",
	    description = "Get a paginated and sorted list of follow items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of follow",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No follow found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<Follow>>> getAllFollows(
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
	    @RequestBody FollowSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<Follow> follows = followService.getAllFollow(pageable, object);
	
	    String message = "follow retrieved successfully";
	
	    if(!follows.hasContent()) {
	            message = "No follow found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Follow>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            follows
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get follow by ID",
	    description = "Retrieve a specific follow item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the follow",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Follow not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<Follow>> getFollowById(
	    @Parameter(description = "ID of the follow to retrieve", required = true)
	    @PathVariable Long id
	) {
	    Follow follow = followService.getFollowById(id);
	    RestResponse<Follow> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "follow retrieved successfully", follow);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Follow to CSV",
	     description = "Generate a CSV file from a provided list of Follow objects."
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
	public ResponseEntity<byte[]> exportFollowToCsv(@RequestBody List<Follow> follow) {
	  
	  String csvContent = followService.exportFollowToCSV(follow);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new follow",
 
	    description = "Create a new follow item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Follow created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid follow supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createFollow(
	    @Parameter(description = "Follow object to be created", required = true)
	    @RequestBody @Valid Follow follow
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
	    Follow newFollow = followService.createFollow(follow);
	    RestResponse<Follow> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "follow created successfully", newFollow);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing follow",

	    description = "Update an existing follow item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Follow updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Follow not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid follow supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateFollow(
	    @Parameter(description = "ID of the follow to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated follow object", required = true)
	    @RequestBody @Valid Follow follow
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
	    Follow updateFollow = followService.updateFollow(id, follow);
	    RestResponse<Follow> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "follow updated successfully", updateFollow);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete follow",

	    description = "Delete a follow item by its ID. Returns success even if follow was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Follow deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteFollowById(
	    @Parameter(description = "ID of the follow to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			followService.getFollowById(id);
			followService.deleteFollow(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"follow deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"follow already deleted or does not exist",
					null
				)
			);
		}
	}



}
