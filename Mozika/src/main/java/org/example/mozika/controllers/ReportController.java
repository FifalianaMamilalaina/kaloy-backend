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
import org.example.mozika.models.Report;
import org.example.mozika.models.dto.ReportSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.ReportService;
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
@RequestMapping("/reports")
@Tag(name = "Report", description = "Report Management APIs")
public class ReportController  {
	private final ReportService reportService;

	public ReportController(ReportService reportService) {
	   this.reportService = reportService;
	}

	@Operation(
	    summary = "Retrieve all report",
	    description = "Get a paginated and sorted list of report items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of report",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No report found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<Report>>> getAllReports(
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
	    Page<Report> reports = reportService.getAllReport(pageable);
	
	    String message = "report retrieved successfully";
	
	    if(!reports.hasContent()) {
	            message = "No report found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Report>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            reports
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all report",
	    description = "Get a paginated and sorted list of report items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of report",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No report found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<Report>>> getAllReports(
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
	    @RequestBody ReportSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<Report> reports = reportService.getAllReport(pageable, object);
	
	    String message = "report retrieved successfully";
	
	    if(!reports.hasContent()) {
	            message = "No report found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Report>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            reports
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get report by ID",
	    description = "Retrieve a specific report item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the report",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Report not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<Report>> getReportById(
	    @Parameter(description = "ID of the report to retrieve", required = true)
	    @PathVariable Long id
	) {
	    Report report = reportService.getReportById(id);
	    RestResponse<Report> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "report retrieved successfully", report);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Report to CSV",
	     description = "Generate a CSV file from a provided list of Report objects."
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
	public ResponseEntity<byte[]> exportReportToCsv(@RequestBody List<Report> report) {
	  
	  String csvContent = reportService.exportReportToCSV(report);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new report",
 
	    description = "Create a new report item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Report created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid report supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createReport(
	    @Parameter(description = "Report object to be created", required = true)
	    @RequestBody @Valid Report report
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
	    Report newReport = reportService.createReport(report);
	    RestResponse<Report> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "report created successfully", newReport);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing report",

	    description = "Update an existing report item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Report updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Report not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid report supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateReport(
	    @Parameter(description = "ID of the report to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated report object", required = true)
	    @RequestBody @Valid Report report
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
	    Report updateReport = reportService.updateReport(id, report);
	    RestResponse<Report> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "report updated successfully", updateReport);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete report",

	    description = "Delete a report item by its ID. Returns success even if report was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Report deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteReportById(
	    @Parameter(description = "ID of the report to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			reportService.getReportById(id);
			reportService.deleteReport(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"report deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"report already deleted or does not exist",
					null
				)
			);
		}
	}



}
