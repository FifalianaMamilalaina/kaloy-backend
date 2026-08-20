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
import org.example.mozika.models.UserRole;
import org.example.mozika.models.dto.UserRoleSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.UserRoleService;
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
@RequestMapping("/userroles")
@Tag(name = "User role", description = "User role Management APIs")
public class UserRoleController  {
	private final UserRoleService userroleService;

	public UserRoleController(UserRoleService userroleService) {
	   this.userroleService = userroleService;
	}

	@Operation(
	    summary = "Retrieve all user role",
	    description = "Get a paginated and sorted list of user role items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of user role",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No user role found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<UserRole>>> getAllUserRoles(
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
	    Page<UserRole> userroles = userroleService.getAllUserRole(pageable);
	
	    String message = "user role retrieved successfully";
	
	    if(!userroles.hasContent()) {
	            message = "No user role found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<UserRole>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            userroles
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all user role",
	    description = "Get a paginated and sorted list of user role items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of user role",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No user role found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<UserRole>>> getAllUserRoles(
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
	    @RequestBody UserRoleSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<UserRole> userroles = userroleService.getAllUserRole(pageable, object);
	
	    String message = "user role retrieved successfully";
	
	    if(!userroles.hasContent()) {
	            message = "No user role found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<UserRole>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            userroles
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get user role by ID",
	    description = "Retrieve a specific user role item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the user role",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "User role not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<UserRole>> getUserRoleById(
	    @Parameter(description = "ID of the user role to retrieve", required = true)
	    @PathVariable Long id
	) {
	    UserRole userrole = userroleService.getUserRoleById(id);
	    RestResponse<UserRole> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "user role retrieved successfully", userrole);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export User role to CSV",
	     description = "Generate a CSV file from a provided list of User role objects."
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
	public ResponseEntity<byte[]> exportUserRoleToCsv(@RequestBody List<UserRole> userrole) {
	  
	  String csvContent = userroleService.exportUserRoleToCSV(userrole);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new user role",
 
	    description = "Create a new user role item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "User role created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid user role supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createUserRole(
	    @Parameter(description = "User role object to be created", required = true)
	    @RequestBody @Valid UserRole userrole
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
	    UserRole newUserRole = userroleService.createUserRole(userrole);
	    RestResponse<UserRole> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "user role created successfully", newUserRole);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing user role",

	    description = "Update an existing user role item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "User role updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "User role not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid user role supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUserRole(
	    @Parameter(description = "ID of the user role to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated user role object", required = true)
	    @RequestBody @Valid UserRole userrole
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
	    UserRole updateUserRole = userroleService.updateUserRole(id, userrole);
	    RestResponse<UserRole> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "user role updated successfully", updateUserRole);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete user role",

	    description = "Delete a user role item by its ID. Returns success even if user role was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "User role deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteUserRoleById(
	    @Parameter(description = "ID of the user role to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			userroleService.getUserRoleById(id);
			userroleService.deleteUserRole(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"user role deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"user role already deleted or does not exist",
					null
				)
			);
		}
	}



}
