package org.example.mozika.controllers;

import org.example.mozika.dto.OtpRequest;
import org.example.mozika.dto.OtpValidateRequest;
import org.example.mozika.dto.RestResponse;
import org.example.mozika.exception.ResourceNotFoundException;
import org.example.mozika.models.User;
import org.example.mozika.models.VerificationCode;
import org.example.mozika.repositories.UserRepository;
import org.example.mozika.security.JwtUtil;
import org.example.mozika.services.interfaces.OtpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/otp")
public class OtpController {

    private final OtpService otpService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public OtpController(OtpService otpService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.otpService = otpService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/send")
    public ResponseEntity<RestResponse<Map<String, Object>>> send(@RequestBody OtpRequest request) {
        Long userId = jwtUtil.extractUserIdFromOtpToken(request.otpToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));

        VerificationCode code = otpService.generateAndSend(user);

        Map<String, Object> data = Map.of(
                "message", "Code envoyé par " + code.getChannelidVerificationChannels().getName(),
                "destination", code.getDestination(),
                "expiresAt", code.getExpiresAt().toString(),
                "otpToken", jwtUtil.generateOtpToken(userId)
        );
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(HttpStatus.OK, "Code envoyé.", data));
    }

    @PostMapping("/verify")
    public ResponseEntity<RestResponse<Map<String, Object>>> verify(@RequestBody OtpValidateRequest request) {
        Long userId = jwtUtil.extractUserIdFromOtpToken(request.otpToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));

        User verifiedUser = otpService.validateCode(user, request.code());
        userRepository.save(verifiedUser);

        Map<String, Object> data = Map.of(
                "message", "Contact vérifié avec succès.",
                "status", verifiedUser.getStatusidUserStatuses().getName()
        );
        return ResponseEntity.ok(RestResponse.buildSuccessResponse(HttpStatus.OK, "Vérification réussie.", data));
    }
}