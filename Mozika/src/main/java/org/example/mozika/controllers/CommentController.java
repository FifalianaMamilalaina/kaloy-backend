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
import org.example.mozika.models.Comment;
import org.example.mozika.models.dto.CommentSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.CommentService;
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
@RequestMapping("/comments")
@Tag(name = "Comment", description = "Comment Management APIs")
public class CommentController  {
	private final CommentService commentService;

	public CommentController(CommentService commentService) {
	   this.commentService = commentService;
	}

	@Operation(
	    summary = "Retrieve all comment",
	    description = "Get a paginated and sorted list of comment items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of comment",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No comment found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<Comment>>> getAllComments(
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
	    Page<Comment> comments = commentService.getAllComment(pageable);
	
	    String message = "comment retrieved successfully";
	
	    if(!comments.hasContent()) {
	            message = "No comment found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Comment>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            comments
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all comment",
	    description = "Get a paginated and sorted list of comment items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of comment",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No comment found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<Comment>>> getAllComments(
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
	    @RequestBody CommentSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<Comment> comments = commentService.getAllComment(pageable, object);
	
	    String message = "comment retrieved successfully";
	
	    if(!comments.hasContent()) {
	            message = "No comment found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<Comment>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            comments
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get comment by ID",
	    description = "Retrieve a specific comment item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the comment",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Comment not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<Comment>> getCommentById(
	    @Parameter(description = "ID of the comment to retrieve", required = true)
	    @PathVariable Long id
	) {
	    Comment comment = commentService.getCommentById(id);
	    RestResponse<Comment> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "comment retrieved successfully", comment);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export Comment to CSV",
	     description = "Generate a CSV file from a provided list of Comment objects."
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
	public ResponseEntity<byte[]> exportCommentToCsv(@RequestBody List<Comment> comment) {
	  
	  String csvContent = commentService.exportCommentToCSV(comment);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new comment",
 
	    description = "Create a new comment item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "201",
	        description = "Comment created successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid comment supplied"
	    )
	})
	@PostMapping
	public ResponseEntity<?> createComment(
	    @Parameter(description = "Comment object to be created", required = true)
	    @RequestBody @Valid Comment comment
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
	    Comment newComment = commentService.createComment(comment);
	    RestResponse<Comment> response = RestResponse.buildSuccessResponse(HttpStatus.CREATED,
	            "comment created successfully", newComment);
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

 
	@Operation(
	    summary = "Update existing comment",

	    description = "Update an existing comment item with the provided information"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Comment updated successfully",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Comment not found with the provided ID"
	    ),
	    @ApiResponse(
	        responseCode = "400",
	        description = "Invalid comment supplied"
	    )
	})
	@PutMapping("/{id}")
	public ResponseEntity<?> updateComment(
	    @Parameter(description = "ID of the comment to update", required = true)
	    @PathVariable Long id,
	    @Parameter(description = "Updated comment object", required = true)
	    @RequestBody @Valid Comment comment
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
	    Comment updateComment = commentService.updateComment(id, comment);
	    RestResponse<Comment> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "comment updated successfully", updateComment);
	    return ResponseEntity.ok(response);
	}


	@Operation(
	    summary = "Delete comment",

	    description = "Delete a comment item by its ID. Returns success even if comment was already deleted."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Comment deleted successfully or already deleted"
	    )
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<RestResponse<Void>> deleteCommentById(
	    @Parameter(description = "ID of the comment to delete", required = true)
	    @PathVariable Long id
	) {
		try {
			commentService.getCommentById(id);
			commentService.deleteComment(id);
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"comment deleted successfully",
					null
				)
			);
		}catch (ResourceNotFoundException ex) {
			return ResponseEntity.ok(
				RestResponse.buildSuccessResponse(
					HttpStatus.OK,
					"comment already deleted or does not exist",
					null
				)
			);
		}
	}



}
