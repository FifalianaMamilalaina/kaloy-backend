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
import org.example.mozika.models.MemberStatuse;
import org.example.mozika.models.dto.MemberStatuseSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.MemberStatuseService;
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
@RequestMapping("/memberstatuses")
@Tag(name = "Member statuse", description = "Member statuse Management APIs")
public class MemberStatuseController  {
	private final MemberStatuseService memberstatuseService;

	public MemberStatuseController(MemberStatuseService memberstatuseService) {
	   this.memberstatuseService = memberstatuseService;
	}

	@Operation(
	    summary = "Retrieve all member statuse",
	    description = "Get a paginated and sorted list of member statuse items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of member statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No member statuse found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<MemberStatuse>>> getAllMemberStatuses(
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
	    Page<MemberStatuse> memberstatuses = memberstatuseService.getAllMemberStatuse(pageable);
	
	    String message = "member statuse retrieved successfully";
	
	    if(!memberstatuses.hasContent()) {
	            message = "No member statuse found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<MemberStatuse>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            memberstatuses
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all member statuse",
	    description = "Get a paginated and sorted list of member statuse items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of member statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No member statuse found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<MemberStatuse>>> getAllMemberStatuses(
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
	    @RequestBody MemberStatuseSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<MemberStatuse> memberstatuses = memberstatuseService.getAllMemberStatuse(pageable, object);
	
	    String message = "member statuse retrieved successfully";
	
	    if(!memberstatuses.hasContent()) {
	            message = "No member statuse found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<MemberStatuse>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            memberstatuses
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get member statuse by ID",
	    description = "Retrieve a specific member statuse item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the member statuse",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Member statuse not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<MemberStatuse>> getMemberStatuseById(
	    @Parameter(description = "ID of the member statuse to retrieve", required = true)
	    @PathVariable Long id
	) {
	    MemberStatuse memberstatuse = memberstatuseService.getMemberStatuseById(id);
	    RestResponse<MemberStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "member statuse retrieved successfully", memberstatuse);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Member statuse to CSV",
	     description = "Generate a CSV file from a provided list of Member statuse objects."
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
	public ResponseEntity<byte[]> exportMemberStatuseToCsv(@RequestBody List<MemberStatuse> memberstatuse) {
	  
	  String csvContent = memberstatuseService.exportMemberStatuseToCSV(memberstatuse);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new member statuse",
 
	    description = "Create a new member statuse item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Member statuse created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid member statuse supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createMemberStatuse(
	    @Parameter(description = "Member statuse object to be created", required = true)
	    @RequestBody @Valid MemberStatuse memberstatuse
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
	    MemberStatuse newMemberStatuse = memberstatuseService.createMemberStatuse(memberstatuse);
	    RestResponse<MemberStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "member statuse created successfully", newMemberStatuse);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing member statuse",

	    description = "Update an existing member statuse item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Member statuse updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Member statuse not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid member statuse supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateMemberStatuse(
	    @Parameter(description = "ID of the member statuse to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated member statuse object", required = true)
	    @RequestBody @Valid MemberStatuse memberstatuse
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
	    MemberStatuse updateMemberStatuse = memberstatuseService.updateMemberStatuse(id, memberstatuse);
	    RestResponse<MemberStatuse> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "member statuse updated successfully", updateMemberStatuse);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete member statuse",

	    description = "Delete a member statuse item by its ID. Returns success even if member statuse was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Member statuse deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteMemberStatuseById(
	    @Parameter(description = "ID of the member statuse to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			memberstatuseService.getMemberStatuseById(id);
			memberstatuseService.deleteMemberStatuse(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"member statuse deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"member statuse already deleted or does not exist",
					null
				)
			);
		}
	}



}
