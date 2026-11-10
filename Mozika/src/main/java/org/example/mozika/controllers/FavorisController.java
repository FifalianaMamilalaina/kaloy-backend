package org.example.mozika.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.example.mozika.dto.RestResponse;
import org.example.mozika.models.dto.FavorisAlbumResponse;
import org.example.mozika.models.dto.FavorisArtisteResponse;
import org.example.mozika.models.dto.FavorisSongResponse;
import org.example.mozika.services.interfaces.FavorisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favoris/me")
@Tag(name = "Favoris", description = "Gestion des favoris de l'utilisateur connecté")
@SecurityRequirement(name = "bearerAuth")
public class FavorisController {

    private final FavorisService favorisService;

    public FavorisController(FavorisService favorisService) {
        this.favorisService = favorisService;
    }

    // ── Chansons ──────────────────────────────────────────────────────────────

    @Operation(summary = "Lister les chansons favorites")
    @GetMapping("/chansons")
    public ResponseEntity<RestResponse<List<FavorisSongResponse>>> getMesFavorisChansons(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<FavorisSongResponse> data = favorisService.getMesFavorisChansons(userDetails.getUsername());
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "Chansons favorites récupérées.", data));
    }

    @Operation(summary = "Aimer une chanson (idempotent)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Chanson ajoutée aux favoris"),
        @ApiResponse(responseCode = "200", description = "Chanson déjà dans les favoris")
    })
    @PostMapping("/chansons/{songId}")
    public ResponseEntity<RestResponse<FavorisSongResponse>> aimerChanson(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID de la chanson") @PathVariable Long songId) {
        FavorisSongResponse data = favorisService.aimerChanson(userDetails.getUsername(), songId);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestResponse.buildSuccessResponse(
                HttpStatus.CREATED, "Chanson ajoutée aux favoris.", data));
    }

    @Operation(summary = "Retirer une chanson des favoris")
    @DeleteMapping("/chansons/{songId}")
    public ResponseEntity<RestResponse<Void>> retirerChanson(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID de la chanson") @PathVariable Long songId) {
        favorisService.retirerChansonDesFavoris(userDetails.getUsername(), songId);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "Chanson retirée des favoris.", null));
    }

    @Operation(summary = "Vérifier si une chanson est dans les favoris")
    @GetMapping("/chansons/{songId}/etat")
    public ResponseEntity<RestResponse<Map<String, Boolean>>> etatChanson(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID de la chanson") @PathVariable Long songId) {
        boolean liked = favorisService.estFavoriChanson(userDetails.getUsername(), songId);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "État favori chanson.", Map.of("liked", liked)));
    }

    // ── Albums ────────────────────────────────────────────────────────────────

    @Operation(summary = "Lister les albums favoris")
    @GetMapping("/albums")
    public ResponseEntity<RestResponse<List<FavorisAlbumResponse>>> getMesFavorisAlbums(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<FavorisAlbumResponse> data = favorisService.getMesFavorisAlbums(userDetails.getUsername());
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "Albums favoris récupérés.", data));
    }

    @Operation(summary = "Aimer un album (idempotent)")
    @PostMapping("/albums/{albumId}")
    public ResponseEntity<RestResponse<FavorisAlbumResponse>> aimerAlbum(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID de l'album") @PathVariable Long albumId) {
        FavorisAlbumResponse data = favorisService.aimerAlbum(userDetails.getUsername(), albumId);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestResponse.buildSuccessResponse(
                HttpStatus.CREATED, "Album ajouté aux favoris.", data));
    }

    @Operation(summary = "Retirer un album des favoris")
    @DeleteMapping("/albums/{albumId}")
    public ResponseEntity<RestResponse<Void>> retirerAlbum(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID de l'album") @PathVariable Long albumId) {
        favorisService.retirerAlbumDesFavoris(userDetails.getUsername(), albumId);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "Album retiré des favoris.", null));
    }

    @Operation(summary = "Vérifier si un album est dans les favoris")
    @GetMapping("/albums/{albumId}/etat")
    public ResponseEntity<RestResponse<Map<String, Boolean>>> etatAlbum(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID de l'album") @PathVariable Long albumId) {
        boolean liked = favorisService.estFavoriAlbum(userDetails.getUsername(), albumId);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "État favori album.", Map.of("liked", liked)));
    }

    // ── Artistes ──────────────────────────────────────────────────────────────

    @Operation(summary = "Lister les artistes favoris")
    @GetMapping("/artistes")
    public ResponseEntity<RestResponse<List<FavorisArtisteResponse>>> getMesFavorisArtistes(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<FavorisArtisteResponse> data = favorisService.getMesFavorisArtistes(userDetails.getUsername());
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "Artistes favoris récupérés.", data));
    }

    @Operation(summary = "Aimer un artiste (idempotent)")
    @PostMapping("/artistes/{artistId}")
    public ResponseEntity<RestResponse<FavorisArtisteResponse>> aimerArtiste(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID de l'artiste") @PathVariable Long artistId) {
        FavorisArtisteResponse data = favorisService.aimerArtiste(userDetails.getUsername(), artistId);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestResponse.buildSuccessResponse(
                HttpStatus.CREATED, "Artiste ajouté aux favoris.", data));
    }

    @Operation(summary = "Retirer un artiste des favoris")
    @DeleteMapping("/artistes/{artistId}")
    public ResponseEntity<RestResponse<Void>> retirerArtiste(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID de l'artiste") @PathVariable Long artistId) {
        favorisService.retirerArtisteDesFavoris(userDetails.getUsername(), artistId);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "Artiste retiré des favoris.", null));
    }

    @Operation(summary = "Vérifier si un artiste est dans les favoris")
    @GetMapping("/artistes/{artistId}/etat")
    public ResponseEntity<RestResponse<Map<String, Boolean>>> etatArtiste(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID de l'artiste") @PathVariable Long artistId) {
        boolean liked = favorisService.estFavoriArtiste(userDetails.getUsername(), artistId);
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(
                HttpStatus.OK, "État favori artiste.", Map.of("liked", liked)));
    }
}
