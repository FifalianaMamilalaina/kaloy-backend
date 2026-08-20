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
import org.example.mozika.models.SearchHistory;
import org.example.mozika.models.dto.SearchHistorySearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.SearchHistoryService;
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
@RequestMapping("/searchhistorys")
@Tag(name = "Search history", description = "Search history Management APIs")
public class SearchHistoryController  {
	private final SearchHistoryService searchhistoryService;

	public SearchHistoryController(SearchHistoryService searchhistoryService) {
	   this.searchhistoryService = searchhistoryService;
	}

	@Operation(
	    summary = "Retrieve all search history",
	    description = "Get a paginated and sorted list of search history items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of search history",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No search history found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<SearchHistory>>> getAllSearchHistorys(
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
	    Page<SearchHistory> searchhistorys = searchhistoryService.getAllSearchHistory(pageable);
	
	    String message = "search history retrieved successfully";
	
	    if(!searchhistorys.hasContent()) {
	            message = "No search history found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<SearchHistory>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            searchhistorys
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all search history",
	    description = "Get a paginated and sorted list of search history items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of search history",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No search history found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<SearchHistory>>> getAllSearchHistorys(
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
	    @RequestBody SearchHistorySearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<SearchHistory> searchhistorys = searchhistoryService.getAllSearchHistory(pageable, object);
	
	    String message = "search history retrieved successfully";
	
	    if(!searchhistorys.hasContent()) {
	            message = "No search history found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<SearchHistory>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            searchhistorys
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get search history by ID",
	    description = "Retrieve a specific search history item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the search history",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Search history not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<SearchHistory>> getSearchHistoryById(
	    @Parameter(description = "ID of the search history to retrieve", required = true)
	    @PathVariable Long id
	) {
	    SearchHistory searchhistory = searchhistoryService.getSearchHistoryById(id);
	    RestResponse<SearchHistory> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "search history retrieved successfully", searchhistory);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Search history to CSV",
	     description = "Generate a CSV file from a provided list of Search history objects."
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
	public ResponseEntity<byte[]> exportSearchHistoryToCsv(@RequestBody List<SearchHistory> searchhistory) {
	  
	  String csvContent = searchhistoryService.exportSearchHistoryToCSV(searchhistory);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new search history",
 
	    description = "Create a new search history item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Search history created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid search history supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createSearchHistory(
	    @Parameter(description = "Search history object to be created", required = true)
	    @RequestBody @Valid SearchHistory searchhistory
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
	    SearchHistory newSearchHistory = searchhistoryService.createSearchHistory(searchhistory);
	    RestResponse<SearchHistory> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "search history created successfully", newSearchHistory);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing search history",

	    description = "Update an existing search history item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Search history updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Search history not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid search history supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateSearchHistory(
	    @Parameter(description = "ID of the search history to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated search history object", required = true)
	    @RequestBody @Valid SearchHistory searchhistory
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
	    SearchHistory updateSearchHistory = searchhistoryService.updateSearchHistory(id, searchhistory);
	    RestResponse<SearchHistory> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "search history updated successfully", updateSearchHistory);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete search history",

	    description = "Delete a search history item by its ID. Returns success even if search history was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Search history deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteSearchHistoryById(
	    @Parameter(description = "ID of the search history to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			searchhistoryService.getSearchHistoryById(id);
			searchhistoryService.deleteSearchHistory(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"search history deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"search history already deleted or does not exist",
					null
				)
			);
		}
	}



}
