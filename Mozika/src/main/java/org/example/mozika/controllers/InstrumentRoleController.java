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
import org.example.mozika.models.InstrumentRole;
import org.example.mozika.models.dto.InstrumentRoleSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.InstrumentRoleService;
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
@RequestMapping("/instrumentroles")
@Tag(name = "Instrument role", description = "Instrument role Management APIs")
public class InstrumentRoleController  {
	private final InstrumentRoleService instrumentroleService;

	public InstrumentRoleController(InstrumentRoleService instrumentroleService) {
	   this.instrumentroleService = instrumentroleService;
	}

	@Operation(
	    summary = "Retrieve all instrument role",
	    description = "Get a paginated and sorted list of instrument role items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of instrument role",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No instrument role found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<InstrumentRole>>> getAllInstrumentRoles(
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
	    Page<InstrumentRole> instrumentroles = instrumentroleService.getAllInstrumentRole(pageable);
	
	    String message = "instrument role retrieved successfully";
	
	    if(!instrumentroles.hasContent()) {
	            message = "No instrument role found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<InstrumentRole>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            instrumentroles
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all instrument role",
	    description = "Get a paginated and sorted list of instrument role items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of instrument role",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No instrument role found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<InstrumentRole>>> getAllInstrumentRoles(
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
	    @RequestBody InstrumentRoleSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<InstrumentRole> instrumentroles = instrumentroleService.getAllInstrumentRole(pageable, object);
	
	    String message = "instrument role retrieved successfully";
	
	    if(!instrumentroles.hasContent()) {
	            message = "No instrument role found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<InstrumentRole>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            instrumentroles
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get instrument role by ID",
	    description = "Retrieve a specific instrument role item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the instrument role",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Instrument role not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<InstrumentRole>> getInstrumentRoleById(
	    @Parameter(description = "ID of the instrument role to retrieve", required = true)
	    @PathVariable Long id
	) {
	    InstrumentRole instrumentrole = instrumentroleService.getInstrumentRoleById(id);
	    RestResponse<InstrumentRole> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "instrument role retrieved successfully", instrumentrole);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Instrument role to CSV",
	     description = "Generate a CSV file from a provided list of Instrument role objects."
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
	public ResponseEntity<byte[]> exportInstrumentRoleToCsv(@RequestBody List<InstrumentRole> instrumentrole) {
	  
	  String csvContent = instrumentroleService.exportInstrumentRoleToCSV(instrumentrole);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new instrument role",
 
	    description = "Create a new instrument role item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Instrument role created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid instrument role supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createInstrumentRole(
	    @Parameter(description = "Instrument role object to be created", required = true)
	    @RequestBody @Valid InstrumentRole instrumentrole
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
	    InstrumentRole newInstrumentRole = instrumentroleService.createInstrumentRole(instrumentrole);
	    RestResponse<InstrumentRole> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "instrument role created successfully", newInstrumentRole);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing instrument role",

	    description = "Update an existing instrument role item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Instrument role updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Instrument role not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid instrument role supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateInstrumentRole(
	    @Parameter(description = "ID of the instrument role to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated instrument role object", required = true)
	    @RequestBody @Valid InstrumentRole instrumentrole
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
	    InstrumentRole updateInstrumentRole = instrumentroleService.updateInstrumentRole(id, instrumentrole);
	    RestResponse<InstrumentRole> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "instrument role updated successfully", updateInstrumentRole);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete instrument role",

	    description = "Delete a instrument role item by its ID. Returns success even if instrument role was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Instrument role deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteInstrumentRoleById(
	    @Parameter(description = "ID of the instrument role to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			instrumentroleService.getInstrumentRoleById(id);
			instrumentroleService.deleteInstrumentRole(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"instrument role deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"instrument role already deleted or does not exist",
					null
				)
			);
		}
	}



}
