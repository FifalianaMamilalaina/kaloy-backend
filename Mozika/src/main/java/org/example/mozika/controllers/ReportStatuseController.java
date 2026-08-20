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
import org.example.mozika.models.ReportStatuse;
import org.example.mozika.models.dto.ReportStatuseSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.ReportStatuseService;
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
@RequestMapping("/reportstatuses")
@Tag(name = "Report statuse", description = "Report statuse Management APIs")
public class ReportStatuseController  {
	private final ReportStatuseService reportstatuseService;

	public ReportStatuseController(ReportStatuseService reportstatuseService) {
	   this.reportstatuseService = reportstatuseService;
	}

	@Operation(
	    summary = "Retrieve all report statuse",
	    description = "Get a paginated and sorted list of report statuse items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of report statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No report statuse found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<ReportStatuse>>> getAllReportStatuses(
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
	    Page<ReportStatuse> reportstatuses = reportstatuseService.getAllReportStatuse(pageable);
	
	    String message = "report statuse retrieved successfully";
	
	    if(!reportstatuses.hasContent()) {
	            message = "No report statuse found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<ReportStatuse>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            reportstatuses
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all report statuse",
	    description = "Get a paginated and sorted list of report statuse items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of report statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No report statuse found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<ReportStatuse>>> getAllReportStatuses(
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
	    @RequestBody ReportStatuseSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<ReportStatuse> reportstatuses = reportstatuseService.getAllReportStatuse(pageable, object);
	
	    String message = "report statuse retrieved successfully";
	
	    if(!reportstatuses.hasContent()) {
	            message = "No report statuse found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<ReportStatuse>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            reportstatuses
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get report statuse by ID",
	    description = "Retrieve a specific report statuse item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the report statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Report statuse not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<ReportStatuse>> getReportStatuseById(
	    @Parameter(description = "ID of the report statuse to retrieve", required = true)
	    @PathVariable Long id
	) {
	    ReportStatuse reportstatuse = reportstatuseService.getReportStatuseById(id);
	    RestResponse<ReportStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "report statuse retrieved successfully", reportstatuse);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Report statuse to CSV",
	     description = "Generate a CSV file from a provided list of Report statuse objects."
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
	public ResponseEntity<byte[]> exportReportStatuseToCsv(@RequestBody List<ReportStatuse> reportstatuse) {
	  
	  String csvContent = reportstatuseService.exportReportStatuseToCSV(reportstatuse);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new report statuse",
 
	    description = "Create a new report statuse item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Report statuse created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid report statuse supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createReportStatuse(
	    @Parameter(description = "Report statuse object to be created", required = true)
	    @RequestBody @Valid ReportStatuse reportstatuse
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
	    ReportStatuse newReportStatuse = reportstatuseService.createReportStatuse(reportstatuse);
	    RestResponse<ReportStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "report statuse created successfully", newReportStatuse);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing report statuse",

	    description = "Update an existing report statuse item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Report statuse updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Report statuse not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid report statuse supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateReportStatuse(
	    @Parameter(description = "ID of the report statuse to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated report statuse object", required = true)
	    @RequestBody @Valid ReportStatuse reportstatuse
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
	    ReportStatuse updateReportStatuse = reportstatuseService.updateReportStatuse(id, reportstatuse);
	    RestResponse<ReportStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "report statuse updated successfully", updateReportStatuse);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete report statuse",

	    description = "Delete a report statuse item by its ID. Returns success even if report statuse was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Report statuse deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteReportStatuseById(
	    @Parameter(description = "ID of the report statuse to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			reportstatuseService.getReportStatuseById(id);
			reportstatuseService.deleteReportStatuse(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"report statuse deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"report statuse already deleted or does not exist",
					null
				)
			);
		}
	}



}
