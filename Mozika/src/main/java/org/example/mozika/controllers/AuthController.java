package org.example.mozika.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.mozika.dto.ChangeContactRequest;
import org.example.mozika.dto.LoginRequest;
import org.example.mozika.dto.RegisterRequest;
import org.example.mozika.dto.RestResponse;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.User;
import org.example.mozika.models.VerificationCode;
import org.example.mozika.repositories.UserRepository;
import org.example.mozika.security.AuthContext;
import org.example.mozika.security.JwtUtil;
import org.example.mozika.services.interfaces.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final AuthContext authContext;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, JwtUtil jwtUtil,
                           AuthContext authContext, UserRepository userRepository) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.authContext = authContext;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<RestResponse<Map<String, Object>>> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);
        Map<String, Object> data = Map.of(
                "id", user.getId(),
                "email", user.getEmail() != null ? user.getEmail() : "",
                "phone", user.getPhone() != null ? user.getPhone() : "",
                "role", user.getRoleidUserRoles().getName(),
                "status", user.getStatusidUserStatuses().getName(),
                "otpToken", jwtUtil.generateOtpToken(user.getId())
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestResponse.buildSuccessResponse(HttpStatus.CREATED, "Compte créé avec succès.", data));
    }

    @PostMapping("/login")
    public ResponseEntity<RestResponse<Map<String, Object>>> login(@RequestBody LoginRequest request) {
        User user = authService.login(request);
        String token = jwtUtil.generateToken(user.getId(), user.getRoleidUserRoles().getName());

        Map<String, Object> data = Map.of(
                "token", token,
                "userId", user.getId(),
                "role", user.getRoleidUserRoles().getName()
        );
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(HttpStatus.OK, "Connexion réussie.", data));
    }

    @GetMapping("/me")
    public ResponseEntity<RestResponse<Map<String, Object>>> me(HttpServletRequest request) {
        Long userId = authContext.requireUserId(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));

        Map<String, Object> data = Map.of(
                "id", user.getId(),
                "email", user.getEmail() != null ? user.getEmail() : "",
                "role", user.getRoleidUserRoles().getName(),
                "status", user.getStatusidUserStatuses().getName()
        );
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(HttpStatus.OK, "Profil récupéré.", data));
    }

    @PostMapping("/change-contact")
    public ResponseEntity<RestResponse<Map<String, Object>>> changeContact(HttpServletRequest request,
                                                                            @RequestBody ChangeContactRequest body) {
        Long userId = authContext.requireUserId(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));

        VerificationCode code = authService.requestContactChange(user, body);

        Map<String, Object> data = Map.of(
                "message", "Code envoyé par " + code.getChannelidVerificationChannels().getName(),
                "destination", code.getDestination(),
                "expiresAt", code.getExpiresAt().toString(),
                "otpToken", jwtUtil.generateOtpToken(userId)
        );
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(HttpStatus.OK, "Code envoyé.", data));
    }
}   