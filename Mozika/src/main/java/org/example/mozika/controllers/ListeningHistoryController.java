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
import org.example.mozika.models.ListeningHistory;
import org.example.mozika.models.dto.ListeningHistorySearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.ListeningHistoryService;
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
@RequestMapping("/listeninghistorys")
@Tag(name = "Listening history", description = "Listening history Management APIs")
public class ListeningHistoryController  {
	private final ListeningHistoryService listeninghistoryService;

	public ListeningHistoryController(ListeningHistoryService listeninghistoryService) {
	   this.listeninghistoryService = listeninghistoryService;
	}

	@Operation(
	    summary = "Retrieve all listening history",
	    description = "Get a paginated and sorted list of listening history items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of listening history",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No listening history found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<ListeningHistory>>> getAllListeningHistorys(
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
	    Page<ListeningHistory> listeninghistorys = listeninghistoryService.getAllListeningHistory(pageable);
	
	    String message = "listening history retrieved successfully";
	
	    if(!listeninghistorys.hasContent()) {
	            message = "No listening history found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<ListeningHistory>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            listeninghistorys
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all listening history",
	    description = "Get a paginated and sorted list of listening history items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of listening history",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No listening history found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<ListeningHistory>>> getAllListeningHistorys(
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
	    @RequestBody ListeningHistorySearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<ListeningHistory> listeninghistorys = listeninghistoryService.getAllListeningHistory(pageable, object);
	
	    String message = "listening history retrieved successfully";
	
	    if(!listeninghistorys.hasContent()) {
	            message = "No listening history found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<ListeningHistory>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            listeninghistorys
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get listening history by ID",
	    description = "Retrieve a specific listening history item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the listening history",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Listening history not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<ListeningHistory>> getListeningHistoryById(
	    @Parameter(description = "ID of the listening history to retrieve", required = true)
	    @PathVariable Long id
	) {
	    ListeningHistory listeninghistory = listeninghistoryService.getListeningHistoryById(id);
	    RestResponse<ListeningHistory> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "listening history retrieved successfully", listeninghistory);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Listening history to CSV",
	     description = "Generate a CSV file from a provided list of Listening history objects."
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
	public ResponseEntity<byte[]> exportListeningHistoryToCsv(@RequestBody List<ListeningHistory> listeninghistory) {
	  
	  String csvContent = listeninghistoryService.exportListeningHistoryToCSV(listeninghistory);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new listening history",
 
	    description = "Create a new listening history item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Listening history created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid listening history supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createListeningHistory(
	    @Parameter(description = "Listening history object to be created", required = true)
	    @RequestBody @Valid ListeningHistory listeninghistory
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
	    ListeningHistory newListeningHistory = listeninghistoryService.createListeningHistory(listeninghistory);
	    RestResponse<ListeningHistory> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "listening history created successfully", newListeningHistory);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing listening history",

	    description = "Update an existing listening history item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Listening history updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Listening history not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid listening history supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateListeningHistory(
	    @Parameter(description = "ID of the listening history to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated listening history object", required = true)
	    @RequestBody @Valid ListeningHistory listeninghistory
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
	    ListeningHistory updateListeningHistory = listeninghistoryService.updateListeningHistory(id, listeninghistory);
	    RestResponse<ListeningHistory> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "listening history updated successfully", updateListeningHistory);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete listening history",

	    description = "Delete a listening history item by its ID. Returns success even if listening history was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Listening history deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteListeningHistoryById(
	    @Parameter(description = "ID of the listening history to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			listeninghistoryService.getListeningHistoryById(id);
			listeninghistoryService.deleteListeningHistory(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"listening history deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"listening history already deleted or does not exist",
					null
				)
			);
		}
	}



}
