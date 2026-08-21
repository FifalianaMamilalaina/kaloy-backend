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
import org.example.mozika.models.UserStatuse;
import org.example.mozika.models.dto.UserStatuseSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.UserStatuseService;
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
@RequestMapping("/userstatuses")
@Tag(name = "User statuse", description = "User statuse Management APIs")
public class UserStatuseController  {
	private final UserStatuseService userstatuseService;

	public UserStatuseController(UserStatuseService userstatuseService) {
	   this.userstatuseService = userstatuseService;
	}

	@Operation(
	    summary = "Retrieve all user statuse",
	    description = "Get a paginated and sorted list of user statuse items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of user statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No user statuse found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<UserStatuse>>> getAllUserStatuses(
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
	    Page<UserStatuse> userstatuses = userstatuseService.getAllUserStatuse(pageable);
	
	    String message = "user statuse retrieved successfully";
	
	    if(!userstatuses.hasContent()) {
	            message = "No user statuse found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<UserStatuse>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            userstatuses
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all user statuse",
	    description = "Get a paginated and sorted list of user statuse items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of user statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No user statuse found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<UserStatuse>>> getAllUserStatuses(
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
	    @RequestBody UserStatuseSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<UserStatuse> userstatuses = userstatuseService.getAllUserStatuse(pageable, object);
	
	    String message = "user statuse retrieved successfully";
	
	    if(!userstatuses.hasContent()) {
	            message = "No user statuse found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<UserStatuse>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            userstatuses
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get user statuse by ID",
	    description = "Retrieve a specific user statuse item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the user statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "User statuse not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<UserStatuse>> getUserStatuseById(
	    @Parameter(description = "ID of the user statuse to retrieve", required = true)
	    @PathVariable Long id
	) {
	    UserStatuse userstatuse = userstatuseService.getUserStatuseById(id);
	    RestResponse<UserStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "user statuse retrieved successfully", userstatuse);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export User statuse to CSV",
	     description = "Generate a CSV file from a provided list of User statuse objects."
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
	public ResponseEntity<byte[]> exportUserStatuseToCsv(@RequestBody List<UserStatuse> userstatuse) {
	  
	  String csvContent = userstatuseService.exportUserStatuseToCSV(userstatuse);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new user statuse",
 
	    description = "Create a new user statuse item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "User statuse created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid user statuse supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createUserStatuse(
	    @Parameter(description = "User statuse object to be created", required = true)
	    @RequestBody @Valid UserStatuse userstatuse
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
	    UserStatuse newUserStatuse = userstatuseService.createUserStatuse(userstatuse);
	    RestResponse<UserStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "user statuse created successfully", newUserStatuse);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing user statuse",

	    description = "Update an existing user statuse item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "User statuse updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "User statuse not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid user statuse supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUserStatuse(
	    @Parameter(description = "ID of the user statuse to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated user statuse object", required = true)
	    @RequestBody @Valid UserStatuse userstatuse
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
	    UserStatuse updateUserStatuse = userstatuseService.updateUserStatuse(id, userstatuse);
	    RestResponse<UserStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "user statuse updated successfully", updateUserStatuse);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete user statuse",

	    description = "Delete a user statuse item by its ID. Returns success even if user statuse was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "User statuse deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteUserStatuseById(
	    @Parameter(description = "ID of the user statuse to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			userstatuseService.getUserStatuseById(id);
			userstatuseService.deleteUserStatuse(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"user statuse deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"user statuse already deleted or does not exist",
					null
				)
			);
		}
	}



}
