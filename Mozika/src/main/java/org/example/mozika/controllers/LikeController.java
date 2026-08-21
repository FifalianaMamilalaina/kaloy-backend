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
import org.example.mozika.models.Like;
import org.example.mozika.models.dto.LikeSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.LikeService;
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
@RequestMapping("/likes")
@Tag(name = "Like", description = "Like Management APIs")
public class LikeController  {
	private final LikeService likeService;

	public LikeController(LikeService likeService) {
	   this.likeService = likeService;
	}

	@Operation(
	    summary = "Retrieve all like",
	    description = "Get a paginated and sorted list of like items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of like",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No like found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<Like>>> getAllLikes(
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
	    Page<Like> likes = likeService.getAllLike(pageable);
	
	    String message = "like retrieved successfully";
	
	    if(!likes.hasContent()) {
	            message = "No like found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Like>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            likes
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all like",
	    description = "Get a paginated and sorted list of like items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of like",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No like found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<Like>>> getAllLikes(
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
	    @RequestBody LikeSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<Like> likes = likeService.getAllLike(pageable, object);
	
	    String message = "like retrieved successfully";
	
	    if(!likes.hasContent()) {
	            message = "No like found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Like>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            likes
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get like by ID",
	    description = "Retrieve a specific like item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the like",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Like not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<Like>> getLikeById(
	    @Parameter(description = "ID of the like to retrieve", required = true)
	    @PathVariable Long id
	) {
	    Like like = likeService.getLikeById(id);
	    RestResponse<Like> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "like retrieved successfully", like);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Like to CSV",
	     description = "Generate a CSV file from a provided list of Like objects."
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
	public ResponseEntity<byte[]> exportLikeToCsv(@RequestBody List<Like> like) {
	  
	  String csvContent = likeService.exportLikeToCSV(like);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new like",
 
	    description = "Create a new like item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Like created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid like supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createLike(
	    @Parameter(description = "Like object to be created", required = true)
	    @RequestBody @Valid Like like
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
	    Like newLike = likeService.createLike(like);
	    RestResponse<Like> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "like created successfully", newLike);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing like",

	    description = "Update an existing like item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Like updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Like not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid like supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateLike(
	    @Parameter(description = "ID of the like to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated like object", required = true)
	    @RequestBody @Valid Like like
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
	    Like updateLike = likeService.updateLike(id, like);
	    RestResponse<Like> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "like updated successfully", updateLike);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete like",

	    description = "Delete a like item by its ID. Returns success even if like was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Like deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteLikeById(
	    @Parameter(description = "ID of the like to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			likeService.getLikeById(id);
			likeService.deleteLike(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"like deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"like already deleted or does not exist",
					null
				)
			);
		}
	}



}
