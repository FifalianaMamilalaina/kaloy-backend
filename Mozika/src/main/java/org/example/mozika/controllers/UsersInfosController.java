package org.example.mozika.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.example.mozika.models.UsersInfos;
import org.example.mozika.models.dto.UsersInfosSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.UsersInfosService;
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
@RequestMapping("/users-infos")
@Tag(name = "UsersInfos", description = "Users Infos Management APIs")
public class UsersInfosController {

    private final UsersInfosService usersInfosService;

    public UsersInfosController(UsersInfosService usersInfosService) {
        this.usersInfosService = usersInfosService;
    }

    @Operation(
        summary = "Retrieve all users infos",
        description = "Get a paginated and sorted list of users infos items."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the paginated list of users infos",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "No users infos found")
    })
    @GetMapping
    public ResponseEntity<RestResponse<Page<UsersInfos>>> getAllUsersInfos(
        @Parameter(description = "Page number (0-indexed)", example = "0")
        @RequestParam(defaultValue = "0") int page,

        @Parameter(description = "Size of the page", example = "10")
        @RequestParam(defaultValue = "10") int size,

        @Parameter(description = "Sorting criteria: property,(asc|desc). Example: id,asc", example = "id,asc")
        @RequestParam(defaultValue = "id,asc") String sortParam) {

        Sort sortObj = WebUtils.createSortObject(sortParam);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<UsersInfos> usersInfos = usersInfosService.getAllUsersInfos(pageable);

        String message = "Users infos retrieved successfully";
        if (!usersInfos.hasContent()) {
            throw new ResourceNotFoundException("No users infos found");
        }

        return ResponseEntity.ok(RestResponse.buildSuccessResponse(HttpStatus.OK, message, usersInfos));
    }

    @Operation(
        summary = "Search users infos",
        description = "Get a paginated and filtered list of users infos."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved filtered results",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "No users infos found matching criteria")
    })
    @PostMapping("/search")
    public ResponseEntity<RestResponse<Page<UsersInfos>>> searchUsersInfos(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id,asc") String sortParam,
        @RequestBody UsersInfosSearch object) {

        Sort sortObj = WebUtils.createSortObject(sortParam);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<UsersInfos> usersInfos = usersInfosService.getAllUsersInfos(pageable, object);

        String message = "Users infos retrieved successfully";
        if (!usersInfos.hasContent()) {
            throw new ResourceNotFoundException("No users infos found");
        }

        return ResponseEntity.ok(RestResponse.buildSuccessResponse(HttpStatus.OK, message, usersInfos));
    }

    @Operation(summary = "Get users infos by ID", description = "Retrieve a specific users infos item by its ID")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the users infos",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Users infos not found with the provided ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<UsersInfos>> getUsersInfosById(
        @Parameter(description = "ID of the users infos to retrieve", required = true)
        @PathVariable Long id) {

        UsersInfos usersInfos = usersInfosService.getUsersInfosById(id);
        return ResponseEntity.ok(
            RestResponse.buildSuccessResponse(HttpStatus.OK, "Users infos retrieved successfully", usersInfos)
        );
    }

    @Operation(summary = "Export users infos to CSV", description = "Generate a CSV file from a list of users infos objects.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "CSV file successfully generated", content = @Content(mediaType = "text/csv")),
        @ApiResponse(responseCode = "400", description = "Invalid or empty list provided")
    })
    @PostMapping(value = "/export/csv", consumes = "application/json", produces = "text/csv")
    public ResponseEntity<byte[]> exportUsersInfosToCsv(@RequestBody List<UsersInfos> usersInfos) {
        String csvContent = usersInfosService.exportUsersInfosToCSV(usersInfos);
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=users-infos.csv")
            .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
            .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    @Operation(summary = "Create new users infos", description = "Create a new users infos record")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Users infos created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<?> createUsersInfos(
        @Parameter(description = "UsersInfos object to be created", required = true)
        @RequestBody @Valid UsersInfos usersInfos,
        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            HashMap<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(
                RestResponse.buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors)
            );
        }

        try {
            UsersInfos created = usersInfosService.createUsersInfos(usersInfos);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                RestResponse.buildSuccessResponse(HttpStatus.CREATED, "Users infos created successfully", created)
            );
        } catch (DataIntegrityViolationException ex) {
            String message = String.format("A database error occurred: %s",
                ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage());
            return ResponseEntity.badRequest().body(
                RestResponse.buildErrorResponse(HttpStatus.BAD_REQUEST, message, null)
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                RestResponse.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred: " + ex.getMessage(), null)
            );
        }
    }

    @Operation(summary = "Update existing users infos", description = "Update an existing users infos record")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Users infos updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Users infos not found"),
        @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsersInfos(
        @Parameter(description = "ID of the users infos to update", required = true)
        @PathVariable Long id,
        @Parameter(description = "Updated users infos object", required = true)
        @RequestBody @Valid UsersInfos usersInfos,
        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            HashMap<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(
                RestResponse.buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors)
            );
        }

        try {
            UsersInfos updated = usersInfosService.updateUsersInfos(id, usersInfos);
            return ResponseEntity.ok(
                RestResponse.buildSuccessResponse(HttpStatus.OK, "Users infos updated successfully", updated)
            );
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                RestResponse.buildErrorResponse(HttpStatus.NOT_FOUND,
                    "Users infos not found with id: " + id, null)
            );
        } catch (DataIntegrityViolationException ex) {
            String message = String.format("A database error occurred: %s",
                ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage());
            return ResponseEntity.badRequest().body(
                RestResponse.buildErrorResponse(HttpStatus.BAD_REQUEST, message, null)
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                RestResponse.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred: " + ex.getMessage(), null)
            );
        }
    }

    @Operation(summary = "Delete users infos", description = "Delete an existing users infos record")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Users infos deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Users infos not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse<Void>> deleteUsersInfos(
        @Parameter(description = "ID of the users infos to delete", required = true)
        @PathVariable Long id) {

        try {
            usersInfosService.getUsersInfosById(id);
            usersInfosService.deleteUsersInfos(id);
            return ResponseEntity.ok(
                RestResponse.buildSuccessResponse(HttpStatus.OK, "Users infos deleted successfully", null)
            );
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.ok(
                RestResponse.buildSuccessResponse(HttpStatus.OK,
                    "Users infos already deleted or does not exist", null)
            );
        }
    }
}
