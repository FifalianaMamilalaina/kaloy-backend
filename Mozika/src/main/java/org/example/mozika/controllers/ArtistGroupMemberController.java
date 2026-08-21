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
import org.example.mozika.models.ArtistGroupMember;
import org.example.mozika.models.dto.ArtistGroupMemberSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.ArtistGroupMemberService;
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
@RequestMapping("/artistgroupmembers")
@Tag(name = "Artist group member", description = "Artist group member Management APIs")
public class ArtistGroupMemberController  {
	private final ArtistGroupMemberService artistgroupmemberService;

	public ArtistGroupMemberController(ArtistGroupMemberService artistgroupmemberService) {
	   this.artistgroupmemberService = artistgroupmemberService;
	}

	@Operation(
	    summary = "Retrieve all artist group member",
	    description = "Get a paginated and sorted list of artist group member items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of artist group member",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No artist group member found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<ArtistGroupMember>>> getAllArtistGroupMembers(
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
	    Page<ArtistGroupMember> artistgroupmembers = artistgroupmemberService.getAllArtistGroupMember(pageable);
	
	    String message = "artist group member retrieved successfully";
	
	    if(!artistgroupmembers.hasContent()) {
	            message = "No artist group member found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<ArtistGroupMember>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            artistgroupmembers
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all artist group member",
	    description = "Get a paginated and sorted list of artist group member items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of artist group member",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No artist group member found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<ArtistGroupMember>>> getAllArtistGroupMembers(
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
	    @RequestBody ArtistGroupMemberSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<ArtistGroupMember> artistgroupmembers = artistgroupmemberService.getAllArtistGroupMember(pageable, object);
	
	    String message = "artist group member retrieved successfully";
	
	    if(!artistgroupmembers.hasContent()) {
	            message = "No artist group member found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<ArtistGroupMember>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            artistgroupmembers
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get artist group member by ID",
	    description = "Retrieve a specific artist group member item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the artist group member",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Artist group member not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<ArtistGroupMember>> getArtistGroupMemberById(
	    @Parameter(description = "ID of the artist group member to retrieve", required = true)
	    @PathVariable Long id
	) {
	    ArtistGroupMember artistgroupmember = artistgroupmemberService.getArtistGroupMemberById(id);
	    RestResponse<ArtistGroupMember> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "artist group member retrieved successfully", artistgroupmember);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Artist group member to CSV",
	     description = "Generate a CSV file from a provided list of Artist group member objects."
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
	public ResponseEntity<byte[]> exportArtistGroupMemberToCsv(@RequestBody List<ArtistGroupMember> artistgroupmember) {
	  
	  String csvContent = artistgroupmemberService.exportArtistGroupMemberToCSV(artistgroupmember);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new artist group member",
 
	    description = "Create a new artist group member item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Artist group member created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid artist group member supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createArtistGroupMember(
	    @Parameter(description = "Artist group member object to be created", required = true)
	    @RequestBody @Valid ArtistGroupMember artistgroupmember
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
	    ArtistGroupMember newArtistGroupMember = artistgroupmemberService.createArtistGroupMember(artistgroupmember);
	    RestResponse<ArtistGroupMember> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "artist group member created successfully", newArtistGroupMember);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing artist group member",

	    description = "Update an existing artist group member item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Artist group member updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Artist group member not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid artist group member supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateArtistGroupMember(
	    @Parameter(description = "ID of the artist group member to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated artist group member object", required = true)
	    @RequestBody @Valid ArtistGroupMember artistgroupmember
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
	    ArtistGroupMember updateArtistGroupMember = artistgroupmemberService.updateArtistGroupMember(id, artistgroupmember);
	    RestResponse<ArtistGroupMember> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "artist group member updated successfully", updateArtistGroupMember);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete artist group member",

	    description = "Delete a artist group member item by its ID. Returns success even if artist group member was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Artist group member deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteArtistGroupMemberById(
	    @Parameter(description = "ID of the artist group member to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			artistgroupmemberService.getArtistGroupMemberById(id);
			artistgroupmemberService.deleteArtistGroupMember(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"artist group member deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"artist group member already deleted or does not exist",
					null
				)
			);
		}
	}



}
