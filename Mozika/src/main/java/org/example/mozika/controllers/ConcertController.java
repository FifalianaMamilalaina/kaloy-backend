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
import org.example.mozika.models.Concert;
import org.example.mozika.models.dto.ConcertSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.ConcertService;
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
@RequestMapping("/concerts")
@Tag(name = "Concert", description = "Concert Management APIs")
public class ConcertController  {
	private final ConcertService concertService;

	public ConcertController(ConcertService concertService) {
	   this.concertService = concertService;
	}

	@Operation(
	    summary = "Retrieve all concert",
	    description = "Get a paginated and sorted list of concert items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of concert",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No concert found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<Concert>>> getAllConcerts(
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
	    Page<Concert> concerts = concertService.getAllConcert(pageable);
	
	    String message = "concert retrieved successfully";
	
	    if(!concerts.hasContent()) {
	            message = "No concert found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Concert>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            concerts
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all concert",
	    description = "Get a paginated and sorted list of concert items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of concert",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No concert found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<Concert>>> getAllConcerts(
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
	    @RequestBody ConcertSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<Concert> concerts = concertService.getAllConcert(pageable, object);
	
	    String message = "concert retrieved successfully";
	
	    if(!concerts.hasContent()) {
	            message = "No concert found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Concert>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            concerts
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get concert by ID",
	    description = "Retrieve a specific concert item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the concert",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Concert not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<Concert>> getConcertById(
	    @Parameter(description = "ID of the concert to retrieve", required = true)
	    @PathVariable Long id
	) {
	    Concert concert = concertService.getConcertById(id);
	    RestResponse<Concert> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "concert retrieved successfully", concert);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Concert to CSV",
	     description = "Generate a CSV file from a provided list of Concert objects."
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
	public ResponseEntity<byte[]> exportConcertToCsv(@RequestBody List<Concert> concert) {
	  
	  String csvContent = concertService.exportConcertToCSV(concert);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new concert",
 
	    description = "Create a new concert item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Concert created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid concert supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createConcert(
	    @Parameter(description = "Concert object to be created", required = true)
	    @RequestBody @Valid Concert concert
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
	    Concert newConcert = concertService.createConcert(concert);
	    RestResponse<Concert> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "concert created successfully", newConcert);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing concert",

	    description = "Update an existing concert item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Concert updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Concert not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid concert supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateConcert(
	    @Parameter(description = "ID of the concert to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated concert object", required = true)
	    @RequestBody @Valid Concert concert
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
	    Concert updateConcert = concertService.updateConcert(id, concert);
	    RestResponse<Concert> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "concert updated successfully", updateConcert);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete concert",

	    description = "Delete a concert item by its ID. Returns success even if concert was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Concert deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteConcertById(
	    @Parameter(description = "ID of the concert to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			concertService.getConcertById(id);
			concertService.deleteConcert(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"concert deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"concert already deleted or does not exist",
					null
				)
			);
		}
	}



}
