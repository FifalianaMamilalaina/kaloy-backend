package org.example.mozika.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.mozika.auth.dto.*;
import org.example.mozika.dto.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("teammateAuthController")
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Authentification — inscription, connexion, vérification OTP")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Inscription client", description = "Crée un compte Client et envoie un code OTP par email")
    @PostMapping("/register/client")
    public ResponseEntity<RestResponse<RegisterResponse>> registerClient(
            @RequestBody @Valid RegisterClientRequest request) {
        RegisterResponse result = authService.registerClient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestResponse.buildSuccessResponse(HttpStatus.CREATED, result.getMessage(), result));
    }

    @Operation(summary = "Inscription artiste", description = "Crée un compte Artiste (SOLO ou GROUP) et envoie un code OTP par email")
    @PostMapping("/register/artist")
    public ResponseEntity<RestResponse<RegisterResponse>> registerArtist(
            @RequestBody @Valid RegisterArtistRequest request) {
        RegisterResponse result = authService.registerArtist(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestResponse.buildSuccessResponse(HttpStatus.CREATED, result.getMessage(), result));
    }

    @Operation(summary = "Vérifier le code OTP", description = "Valide le code OTP et retourne un JWT si correct")
    @PostMapping("/verify-otp")
    public ResponseEntity<RestResponse<AuthResponse>> verifyOtp(
            @RequestBody @Valid OtpVerifyRequest request) {
        AuthResponse result = authService.verifyOtp(request);
        return ResponseEntity.ok(
                RestResponse.buildSuccessResponse(HttpStatus.OK, "Compte vérifié avec succès", result));
    }

    @Operation(summary = "Renvoyer le code OTP", description = "Renvoie un nouveau code OTP (anti-abus : 60 secondes entre deux envois)")
    @PostMapping("/resend-otp")
    public ResponseEntity<RestResponse<String>> resendOtp(
            @RequestBody @Valid ResendOtpRequest request) {
        String message = authService.resendOtp(request);
        return ResponseEntity.ok(
                RestResponse.buildSuccessResponse(HttpStatus.OK, message, null));
    }

    @Operation(summary = "Connexion", description = "Authentifie un utilisateur vérifié et retourne un JWT")
    @PostMapping("/login")
    public ResponseEntity<RestResponse<AuthResponse>> login(
            @RequestBody @Valid LoginRequest request) {
        AuthResponse result = authService.login(request);
        return ResponseEntity.ok(
                RestResponse.buildSuccessResponse(HttpStatus.OK, "Connexion réussie", result));
    }
}
