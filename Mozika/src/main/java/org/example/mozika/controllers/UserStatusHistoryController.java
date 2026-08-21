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
import org.example.mozika.models.UserStatusHistory;
import org.example.mozika.models.dto.UserStatusHistorySearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.UserStatusHistoryService;
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
@RequestMapping("/userstatushistorys")
@Tag(name = "User status history", description = "User status history Management APIs")
public class UserStatusHistoryController  {
	private final UserStatusHistoryService userstatushistoryService;

	public UserStatusHistoryController(UserStatusHistoryService userstatushistoryService) {
	   this.userstatushistoryService = userstatushistoryService;
	}

	@Operation(
	    summary = "Retrieve all user status history",
	    description = "Get a paginated and sorted list of user status history items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of user status history",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No user status history found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<UserStatusHistory>>> getAllUserStatusHistorys(
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
	    Page<UserStatusHistory> userstatushistorys = userstatushistoryService.getAllUserStatusHistory(pageable);
	
	    String message = "user status history retrieved successfully";
	
	    if(!userstatushistorys.hasContent()) {
	            message = "No user status history found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<UserStatusHistory>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            userstatushistorys
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all user status history",
	    description = "Get a paginated and sorted list of user status history items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of user status history",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No user status history found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<UserStatusHistory>>> getAllUserStatusHistorys(
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
	    @RequestBody UserStatusHistorySearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<UserStatusHistory> userstatushistorys = userstatushistoryService.getAllUserStatusHistory(pageable, object);
	
	    String message = "user status history retrieved successfully";
	
	    if(!userstatushistorys.hasContent()) {
	            message = "No user status history found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<UserStatusHistory>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            userstatushistorys
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get user status history by ID",
	    description = "Retrieve a specific user status history item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the user status history",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "User status history not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<UserStatusHistory>> getUserStatusHistoryById(
	    @Parameter(description = "ID of the user status history to retrieve", required = true)
	    @PathVariable Long id
	) {
	    UserStatusHistory userstatushistory = userstatushistoryService.getUserStatusHistoryById(id);
	    RestResponse<UserStatusHistory> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "user status history retrieved successfully", userstatushistory);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export User status history to CSV",
	     description = "Generate a CSV file from a provided list of User status history objects."
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
	public ResponseEntity<byte[]> exportUserStatusHistoryToCsv(@RequestBody List<UserStatusHistory> userstatushistory) {
	  
	  String csvContent = userstatushistoryService.exportUserStatusHistoryToCSV(userstatushistory);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new user status history",
 
	    description = "Create a new user status history item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "User status history created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid user status history supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createUserStatusHistory(
	    @Parameter(description = "User status history object to be created", required = true)
	    @RequestBody @Valid UserStatusHistory userstatushistory
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
	    UserStatusHistory newUserStatusHistory = userstatushistoryService.createUserStatusHistory(userstatushistory);
	    RestResponse<UserStatusHistory> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "user status history created successfully", newUserStatusHistory);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing user status history",

	    description = "Update an existing user status history item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "User status history updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "User status history not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid user status history supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUserStatusHistory(
	    @Parameter(description = "ID of the user status history to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated user status history object", required = true)
	    @RequestBody @Valid UserStatusHistory userstatushistory
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
	    UserStatusHistory updateUserStatusHistory = userstatushistoryService.updateUserStatusHistory(id, userstatushistory);
	    RestResponse<UserStatusHistory> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "user status history updated successfully", updateUserStatusHistory);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete user status history",

	    description = "Delete a user status history item by its ID. Returns success even if user status history was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "User status history deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteUserStatusHistoryById(
	    @Parameter(description = "ID of the user status history to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			userstatushistoryService.getUserStatusHistoryById(id);
			userstatushistoryService.deleteUserStatusHistory(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"user status history deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"user status history already deleted or does not exist",
					null
				)
			);
		}
	}



}
