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
import jakarta.servlet.http.HttpServletRequest;
import org.example.mozika.security.AuthContext;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.example.mozika.models.User;
import org.example.mozika.models.dto.UserSearch;
import org.springframework.web.bind.annotation.*;
import org.example.mozika.services.interfaces.UserService;
import org.example.mozika.services.interfaces.ArtistService;
import org.example.mozika.models.dto.ArtistSearch;
import org.example.mozika.models.Artist;
import org.example.mozika.services.interfaces.ClientService;
import org.example.mozika.models.dto.ClientSearch;
import org.example.mozika.models.Client;
import org.example.mozika.services.interfaces.CommentService;
import org.example.mozika.models.dto.CommentSearch;
import org.example.mozika.models.Comment;
import org.example.mozika.services.interfaces.DownloadService;
import org.example.mozika.models.dto.DownloadSearch;
import org.example.mozika.models.Download;
import org.example.mozika.services.interfaces.EventMediaService;
import org.example.mozika.models.dto.EventMediaSearch;
import org.example.mozika.models.EventMedia;
import org.example.mozika.services.interfaces.FollowService;
import org.example.mozika.models.dto.FollowSearch;
import org.example.mozika.models.Follow;
import org.example.mozika.services.interfaces.LikeService;
import org.example.mozika.models.dto.LikeSearch;
import org.example.mozika.models.Like;
import org.example.mozika.services.interfaces.ListeningHistoryService;
import org.example.mozika.models.dto.ListeningHistorySearch;
import org.example.mozika.models.ListeningHistory;
import org.example.mozika.services.interfaces.NotificationPreferenceService;
import org.example.mozika.models.dto.NotificationPreferenceSearch;
import org.example.mozika.models.NotificationPreference;
import org.example.mozika.services.interfaces.NotificationService;
import org.example.mozika.models.dto.NotificationSearch;
import org.example.mozika.models.Notification;
import org.example.mozika.services.interfaces.PlaylistService;
import org.example.mozika.models.dto.PlaylistSearch;
import org.example.mozika.models.Playlist;
import org.example.mozika.services.interfaces.ReportService;
import org.example.mozika.models.dto.ReportSearch;
import org.example.mozika.models.Report;
import org.example.mozika.services.interfaces.SearchHistoryService;
import org.example.mozika.models.dto.SearchHistorySearch;
import org.example.mozika.models.SearchHistory;
import org.example.mozika.services.interfaces.UpNextQueueService;
import org.example.mozika.models.dto.UpNextQueueSearch;
import org.example.mozika.models.UpNextQueue;
import org.example.mozika.services.interfaces.UserStatusHistoryService;
import org.example.mozika.models.dto.UserStatusHistorySearch;
import org.example.mozika.models.UserStatusHistory;
import org.example.mozika.services.interfaces.VerificationCodeService;
import org.example.mozika.models.dto.VerificationCodeSearch;
import org.example.mozika.models.VerificationCode;


import org.example.mozika.models.dto.UserFullDto;

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
@RequestMapping("/users")
@Tag(name = "User", description = "User Management APIs")
public class UserController  {
private final AuthContext authContext;
private final UserService userService;
private final ArtistService artistService;
private final ClientService clientService;
private final CommentService commentService;
private final DownloadService downloadService;
private final EventMediaService eventmediaService;
private final FollowService followService;
private final LikeService likeService;
private final ListeningHistoryService listeninghistoryService;
private final NotificationPreferenceService notificationpreferenceService;
private final NotificationService notificationService;
private final PlaylistService playlistService;
private final ReportService reportService;
private final SearchHistoryService searchhistoryService;
private final UpNextQueueService upnextqueueService;
private final UserStatusHistoryService userstatushistoryService;
private final VerificationCodeService verificationcodeService;


	public UserController(UserService userService, ArtistService artistService, ClientService clientService, CommentService commentService, DownloadService downloadService, EventMediaService eventmediaService, FollowService followService, LikeService likeService, ListeningHistoryService listeninghistoryService, NotificationPreferenceService notificationpreferenceService, NotificationService notificationService, PlaylistService playlistService, ReportService reportService, SearchHistoryService searchhistoryService, UpNextQueueService upnextqueueService, UserStatusHistoryService userstatushistoryService, VerificationCodeService verificationcodeService) {
	   this.userService = userService;
	this.artistService = artistService;
	this.clientService = clientService;
	this.commentService = commentService;
	this.downloadService = downloadService;
	this.eventmediaService = eventmediaService;
	this.followService = followService;
	this.likeService = likeService;
	this.listeninghistoryService = listeninghistoryService;
	this.notificationpreferenceService = notificationpreferenceService;
	this.notificationService = notificationService;
	this.playlistService = playlistService;
	this.reportService = reportService;
	this.searchhistoryService = searchhistoryService;
	this.upnextqueueService = upnextqueueService;
	this.userstatushistoryService = userstatushistoryService;
	this.verificationcodeService = verificationcodeService;
	
	}

	@Operation(
	    summary = "Retrieve all user",
	    description = "Get a paginated and sorted list of user items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of user",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No user found"
	    )
	})
	@GetMapping
	public ResponseEntity<RestResponse<Page<User>>> getAllUsers(
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
	    Page<User> users = userService.getAllUser(pageable);
	
	    String message = "user retrieved successfully";
	
	    if(!users.hasContent()) {
	            message = "No user found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<User>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            users
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Retrieve all user",
	    description = "Get a paginated and sorted list of user items. Returns an empty list if no data is found."
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the paginated list of user",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "204",
	        description = "No user found"
	    )
	})
	@PostMapping("/search")
	public ResponseEntity<RestResponse<Page<User>>> getAllUsers(
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
	    @RequestBody UserSearch object) {
	
	    Sort sortObj = WebUtils.createSortObject(sortParam);
	    Pageable pageable = PageRequest.of(page, size, sortObj);
	    Page<User> users = userService.getAllUser(pageable, object);
	
	    String message = "user retrieved successfully";
	
	    if(!users.hasContent()) {
	            message = "No user found";
	            throw new ResourceNotFoundException(message);
	    }
	
	    RestResponse<Page<User>> response = RestResponse.buildSuccessResponse(
	            HttpStatus.OK,
	            message,
	            users
	    );
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	@Operation(
	    summary = "Get user by ID",
	    description = "Retrieve a specific user item by its ID"
	)
	@ApiResponses({
	    @ApiResponse(
	        responseCode = "200",
	        description = "Successfully retrieved the user",
	        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "User not found with the provided ID"
	    )
	})
	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<User>> getUserById(
	    @Parameter(description = "ID of the user to retrieve", required = true)
	    @PathVariable Long id
	) {
	    User user = userService.getUserById(id);
	    RestResponse<User> response = RestResponse.buildSuccessResponse(HttpStatus.OK,
	            "user retrieved successfully", user);
	    return ResponseEntity.ok(response);
	}
	@Operation(
	     summary = "Export User to CSV",
	     description = "Generate a CSV file from a provided list of User objects."
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
	public ResponseEntity<byte[]> exportUserToCsv(@RequestBody List<User> user) {
	  
	  String csvContent = userService.exportUserToCSV(user);
	  
	  return ResponseEntity.ok()
	   .header("Content-Disposition", "attachment; filename=data.csv")
	   .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
	   .body(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}
 

	@Operation(
	    summary = "Create new user",

	    description = "Create a new user and its details. The operation is atomic: all or nothing."
	)
	@ApiResponses({
	   @ApiResponse(
	     responseCode = "201",
	     description = "User created successfully",
	     content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
	   ),
	   @ApiResponse(
	     responseCode = "400",
	     description = "Validation failed for one or more entities"
	   ),
	   @ApiResponse(
	     responseCode = "500",
	     description = "Internal server error"
	   )
	})
	@PostMapping
	public ResponseEntity<?> createFullUser(
	    @Parameter(description = "User object to be created with artists{{#unless @last}}, {{/unless}}clients{{#unless @last}}, {{/unless}}comments{{#unless @last}}, {{/unless}}downloads{{#unless @last}}, {{/unless}}eventMedias{{#unless @last}}, {{/unless}}follows{{#unless @last}}, {{/unless}}likes{{#unless @last}}, {{/unless}}listeningHistorys{{#unless @last}}, {{/unless}}notificationPreferences{{#unless @last}}, {{/unless}}notifications{{#unless @last}}, {{/unless}}playlists{{#unless @last}}, {{/unless}}reports{{#unless @last}}, {{/unless}}searchHistorys{{#unless @last}}, {{/unless}}upNextQueues{{#unless @last}}, {{/unless}}userStatusHistorys{{#unless @last}}, {{/unless}}verificationCodes{{#unless @last}}, {{/unless}}", required = true)
	    @RequestBody @Valid UserFullDto dto,
	    BindingResult bindingResult
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
	  
	  try {
	    User user = dto.getUser();
	    List<Artist> artists = dto.getArtists();
	    List<Client> clients = dto.getClients();
	    List<Comment> comments = dto.getComments();
	    List<Download> downloads = dto.getDownloads();
	    List<EventMedia> eventMedias = dto.getEventMedias();
	    List<Follow> follows = dto.getFollows();
	    List<Like> likes = dto.getLikes();
	    List<ListeningHistory> listeningHistorys = dto.getListeningHistorys();
	    List<NotificationPreference> notificationPreferences = dto.getNotificationPreferences();
	    List<Notification> notifications = dto.getNotifications();
	    List<Playlist> playlists = dto.getPlaylists();
	    List<Report> reports = dto.getReports();
	    List<SearchHistory> searchHistorys = dto.getSearchHistorys();
	    List<UpNextQueue> upNextQueues = dto.getUpNextQueues();
	    List<UserStatusHistory> userStatusHistorys = dto.getUserStatusHistorys();
	    List<VerificationCode> verificationCodes = dto.getVerificationCodes();
	    
	    User createdUser = userService.createFullUser(user, artists, clients, comments, downloads, eventMedias, follows, likes, listeningHistorys, notificationPreferences, notifications, playlists, reports, searchHistorys, upNextQueues, userStatusHistorys, verificationCodes);
	    RestResponse<User> response = RestResponse.buildSuccessResponse(
	      HttpStatus.CREATED,
	      "User and its details created successfully (Atomic operation)",
	      createdUser
	    );
	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	  } catch (DataIntegrityViolationException ex) {
	    String message = String.format("A database error occurred during the transaction (e.g., constraint violation): %s",
	      	ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage());
	    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
	      HttpStatus.BAD_REQUEST,
	      message,
	      null
	    );
	    return ResponseEntity.badRequest().body(errorResponse);
	  } catch (Exception ex) {
	    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
	      HttpStatus.INTERNAL_SERVER_ERROR,
	      String.format("An unexpected error occurred while creating the user: %s", ex.getMessage()),
	      null
	    );
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	  }
	}

 
	@Operation(
	    summary = "Update existing user",

          description = "Update an existing user and its details. The operation is atomic: all or nothing."
      )
      @ApiResponses({
      @ApiResponse(
      responseCode = "200",
      description = "User updated successfully",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
      ),
      @ApiResponse(
      responseCode = "404",
      description = "User not found with the provided ID"
      ),
      @ApiResponse(
      responseCode = "400",
      description = "Validation failed or database constraint violation"
      ),
      @ApiResponse(
      responseCode = "500",
      description = "Internal server error"
      )
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFullUser(
      @Parameter(description = "ID of the user to update", required = true)
      @PathVariable Long id,
      @Parameter(description = "Updated user object with artists{{#unless @last}}, {{/unless}}clients{{#unless @last}}, {{/unless}}comments{{#unless @last}}, {{/unless}}downloads{{#unless @last}}, {{/unless}}eventMedias{{#unless @last}}, {{/unless}}follows{{#unless @last}}, {{/unless}}likes{{#unless @last}}, {{/unless}}listeningHistorys{{#unless @last}}, {{/unless}}notificationPreferences{{#unless @last}}, {{/unless}}notifications{{#unless @last}}, {{/unless}}playlists{{#unless @last}}, {{/unless}}reports{{#unless @last}}, {{/unless}}searchHistorys{{#unless @last}}, {{/unless}}upNextQueues{{#unless @last}}, {{/unless}}userStatusHistorys{{#unless @last}}, {{/unless}}verificationCodes{{#unless @last}}, {{/unless}}", required = true)
      @RequestBody @Valid UserFullDto dto,
      BindingResult bindingResult
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
      
      try {
        // Check if user exists
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
          RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
          HttpStatus.NOT_FOUND,
          String.format("User not found with id: %s", id),
          null
          );
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        
        User user = dto.getUser();
        List<Artist> artists = dto.getArtists();
        List<Client> clients = dto.getClients();
        List<Comment> comments = dto.getComments();
        List<Download> downloads = dto.getDownloads();
        List<EventMedia> eventMedias = dto.getEventMedias();
        List<Follow> follows = dto.getFollows();
        List<Like> likes = dto.getLikes();
        List<ListeningHistory> listeningHistorys = dto.getListeningHistorys();
        List<NotificationPreference> notificationPreferences = dto.getNotificationPreferences();
        List<Notification> notifications = dto.getNotifications();
        List<Playlist> playlists = dto.getPlaylists();
        List<Report> reports = dto.getReports();
        List<SearchHistory> searchHistorys = dto.getSearchHistorys();
        List<UpNextQueue> upNextQueues = dto.getUpNextQueues();
        List<UserStatusHistory> userStatusHistorys = dto.getUserStatusHistorys();
        List<VerificationCode> verificationCodes = dto.getVerificationCodes();
        
        
        User updatedUser = userService.updateFullUser(
            id, user, artists, clients, comments, downloads, eventMedias, follows, likes, listeningHistorys, notificationPreferences, notifications, playlists, reports, searchHistorys, upNextQueues, userStatusHistorys, verificationCodes
        );
        
        RestResponse<User> response = RestResponse.buildSuccessResponse(
            HttpStatus.OK,
            "User and its details updated successfully (Atomic operation)",
            updatedUser
        );
        return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
        RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.NOT_FOUND,
            String.format("User not found with id: %s", id),
            null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (DataIntegrityViolationException ex) {
        String message = String.format("A database error occurred during the transaction (e.g., constraint violation): %s", 
            ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage());
        RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            message,
            null
        );
        return ResponseEntity.badRequest().body(errorResponse);
      } catch (Exception ex) {
        RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            String.format("An unexpected error occurred while updating the user: %s", ex.getMessage()),
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
      }
    }


	@Operation(
	    summary = "Delete user",

          description = "Delete an existing user and all its associated details. The operation is atomic: all or nothing."
      )
      @ApiResponses({
        @ApiResponse(
          responseCode = "200",
          description = "User deleted successfully"
        ),
        @ApiResponse(
          responseCode = "404",
          description = "User not found with the provided ID"
        ),
        @ApiResponse(
          responseCode = "500",
          description = "Internal server error"
        )
      })
      @DeleteMapping("/{id}")
      public ResponseEntity<RestResponse<Void>> deleteFullUser(
        @Parameter(description = "ID of the user to delete", required = true)
        @PathVariable Long id
      ) {
        try {
          userService.deleteFullUser(id);
          return ResponseEntity.ok(
            RestResponse.buildSuccessResponse(
              HttpStatus.OK,
              "User deleted successfully",
              null
            )
          );
        } catch (ResourceNotFoundException ex) {
          RestResponse<Void> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.NOT_FOUND,
            String.format("User not found with id: %s", id),
            null
          );
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception ex) {
          RestResponse<Void> errorResponse = RestResponse.buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            String.format("Error while performing atomic deletion of user: %s", ex.getMessage()),
            null
          );
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
      } 

@Operation(
  summary = "Get artists attached with a user ID",
  description = "Retrieve a list of artists from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "Artists retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/artists")
public ResponseEntity<?> getArtistsByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  ArtistSearch object = new ArtistSearch();
  object.setUseridUsers(user);
  
  Page<Artist> artistsData = artistService.getAllArtist(pageable, object);
  
  RestResponse<Page<Artist>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "Artists retrieved successfully",
    artistsData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get clients attached with a user ID",
  description = "Retrieve a list of clients from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "Clients retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/clients")
public ResponseEntity<?> getClientsByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  ClientSearch object = new ClientSearch();
  object.setUseridUsers(user);
  
  Page<Client> clientsData = clientService.getAllClient(pageable, object);
  
  RestResponse<Page<Client>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "Clients retrieved successfully",
    clientsData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get comments attached with a user ID",
  description = "Retrieve a list of comments from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "Comments retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/comments")
public ResponseEntity<?> getCommentsByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  CommentSearch object = new CommentSearch();
  object.setAuthoruseridUsers(user);
  
  Page<Comment> commentsData = commentService.getAllComment(pageable, object);
  
  RestResponse<Page<Comment>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "Comments retrieved successfully",
    commentsData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get downloads attached with a user ID",
  description = "Retrieve a list of downloads from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "Downloads retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/downloads")
public ResponseEntity<?> getDownloadsByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  DownloadSearch object = new DownloadSearch();
  object.setUseridUsers(user);
  
  Page<Download> downloadsData = downloadService.getAllDownload(pageable, object);
  
  RestResponse<Page<Download>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "Downloads retrieved successfully",
    downloadsData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get eventMedias attached with a user ID",
  description = "Retrieve a list of eventMedias from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "EventMedias retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/eventmedias")
public ResponseEntity<?> getEventMediasByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  EventMediaSearch object = new EventMediaSearch();
  object.setUploaderuseridUsers(user);
  
  Page<EventMedia> eventMediasData = eventmediaService.getAllEventMedia(pageable, object);
  
  RestResponse<Page<EventMedia>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "EventMedias retrieved successfully",
    eventMediasData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get follows attached with a user ID",
  description = "Retrieve a list of follows from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "Follows retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/follows")
public ResponseEntity<?> getFollowsByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  FollowSearch object = new FollowSearch();
  object.setClientuseridUsers(user);
  
  Page<Follow> followsData = followService.getAllFollow(pageable, object);
  
  RestResponse<Page<Follow>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "Follows retrieved successfully",
    followsData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get likes attached with a user ID",
  description = "Retrieve a list of likes from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "Likes retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/likes")
public ResponseEntity<?> getLikesByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  LikeSearch object = new LikeSearch();
  object.setUseridUsers(user);
  
  Page<Like> likesData = likeService.getAllLike(pageable, object);
  
  RestResponse<Page<Like>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "Likes retrieved successfully",
    likesData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get listeningHistorys attached with a user ID",
  description = "Retrieve a list of listeningHistorys from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "ListeningHistorys retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/listeninghistorys")
public ResponseEntity<?> getListeningHistorysByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  ListeningHistorySearch object = new ListeningHistorySearch();
  object.setUseridUsers(user);
  
  Page<ListeningHistory> listeningHistorysData = listeninghistoryService.getAllListeningHistory(pageable, object);
  
  RestResponse<Page<ListeningHistory>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "ListeningHistorys retrieved successfully",
    listeningHistorysData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get notificationPreferences attached with a user ID",
  description = "Retrieve a list of notificationPreferences from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "NotificationPreferences retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/notificationpreferences")
public ResponseEntity<?> getNotificationPreferencesByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  NotificationPreferenceSearch object = new NotificationPreferenceSearch();
  object.setUseridUsers(user);
  
  Page<NotificationPreference> notificationPreferencesData = notificationpreferenceService.getAllNotificationPreference(pageable, object);
  
  RestResponse<Page<NotificationPreference>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "NotificationPreferences retrieved successfully",
    notificationPreferencesData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get notifications attached with a user ID",
  description = "Retrieve a list of notifications from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "Notifications retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/notifications")
public ResponseEntity<?> getNotificationsByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  NotificationSearch object = new NotificationSearch();
  object.setUseridUsers(user);
  
  Page<Notification> notificationsData = notificationService.getAllNotification(pageable, object);
  
  RestResponse<Page<Notification>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "Notifications retrieved successfully",
    notificationsData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get playlists attached with a user ID",
  description = "Retrieve a list of playlists from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "Playlists retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/playlists")
public ResponseEntity<?> getPlaylistsByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  PlaylistSearch object = new PlaylistSearch();
  object.setOwneruseridUsers(user);
  
  Page<Playlist> playlistsData = playlistService.getAllPlaylist(pageable, object);
  
  RestResponse<Page<Playlist>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "Playlists retrieved successfully",
    playlistsData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get reports attached with a user ID",
  description = "Retrieve a list of reports from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "Reports retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/reports")
public ResponseEntity<?> getReportsByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  ReportSearch object = new ReportSearch();
  object.setReporteruseridUsers(user);
  
  Page<Report> reportsData = reportService.getAllReport(pageable, object);
  
  RestResponse<Page<Report>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "Reports retrieved successfully",
    reportsData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get searchHistorys attached with a user ID",
  description = "Retrieve a list of searchHistorys from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "SearchHistorys retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/searchhistorys")
public ResponseEntity<?> getSearchHistorysByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  SearchHistorySearch object = new SearchHistorySearch();
  object.setUseridUsers(user);
  
  Page<SearchHistory> searchHistorysData = searchhistoryService.getAllSearchHistory(pageable, object);
  
  RestResponse<Page<SearchHistory>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "SearchHistorys retrieved successfully",
    searchHistorysData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get upNextQueues attached with a user ID",
  description = "Retrieve a list of upNextQueues from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "UpNextQueues retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/upnextqueues")
public ResponseEntity<?> getUpNextQueuesByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  UpNextQueueSearch object = new UpNextQueueSearch();
  object.setUseridUsers(user);
  
  Page<UpNextQueue> upNextQueuesData = upnextqueueService.getAllUpNextQueue(pageable, object);
  
  RestResponse<Page<UpNextQueue>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "UpNextQueues retrieved successfully",
    upNextQueuesData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get userStatusHistorys attached with a user ID",
  description = "Retrieve a list of userStatusHistorys from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "UserStatusHistorys retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/userstatushistorys")
public ResponseEntity<?> getUserStatusHistorysByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  UserStatusHistorySearch object = new UserStatusHistorySearch();
  object.setUseridUsers(user);
  
  Page<UserStatusHistory> userStatusHistorysData = userstatushistoryService.getAllUserStatusHistory(pageable, object);
  
  RestResponse<Page<UserStatusHistory>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "UserStatusHistorys retrieved successfully",
    userStatusHistorysData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}
@Operation(
  summary = "Get verificationCodes attached with a user ID",
  description = "Retrieve a list of verificationCodes from a user item ID"
)
@ApiResponses({
  @ApiResponse(
    responseCode = "200",
    description = "VerificationCodes retrieved successfully",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestResponse.class))
  ),
  @ApiResponse(
    responseCode = "404",
    description = "User not found with the provided ID"
  ),
  @ApiResponse(
    responseCode = "500",
    description = "Internal server error"
  )
})
@GetMapping("/{id}/verificationcodes")
public ResponseEntity<?> getVerificationCodesByUser(
  @Parameter(description = "ID of the user to retrieve", required = true)
  @PathVariable Long id,
  @Parameter(description = "Page number (0-indexed)", required = false)
  @RequestParam(defaultValue = "0") int page,
  @Parameter(description = "Size of the page", required = false)
  @RequestParam(defaultValue = "10") int size,
  @Parameter(description = "Sort order", required = false)
  @RequestParam(defaultValue = "id,asc", required = false) String sortParam
) {
try {
  User user = userService.getUserById(id);
  if (user == null) {
    RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
      HttpStatus.NOT_FOUND,
      String.format("User not found with id: %s", id),
      null
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
  Sort sortObj = WebUtils.createSortObject(sortParam);
  Pageable pageable = PageRequest.of(page, size, sortObj);
  VerificationCodeSearch object = new VerificationCodeSearch();
  object.setUseridUsers(user);
  
  Page<VerificationCode> verificationCodesData = verificationcodeService.getAllVerificationCode(pageable, object);
  
  RestResponse<Page<VerificationCode>> response = RestResponse.buildSuccessResponse(
    HttpStatus.OK,
    "VerificationCodes retrieved successfully",
    verificationCodesData
  );
  return ResponseEntity.ok(response);
} catch (ResourceNotFoundException ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.NOT_FOUND,
    ex.getMessage(),
    null
  );
  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
} catch (Exception ex) {
  RestResponse<String> errorResponse = RestResponse.buildErrorResponse(
    HttpStatus.INTERNAL_SERVER_ERROR,
    String.format("Error while retrieving user: %s", ex.getMessage()),
    null
  );
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
}
}


}
