package org.example.mozika.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.mozika.dto.RestResponse;
import org.example.mozika.exception.InternalServerErrorException;
import org.example.mozika.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<RestResponse<Void>> handleEntityNotFoundException(ResourceNotFoundException ex) {
        RestResponse<Void> response = RestResponse.buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
        log.info("Resource not found : {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<RestResponse<Void>> handleInternalServerErrorException(InternalServerErrorException ex) {
        RestResponse<Void> response = RestResponse.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),
                null);
        log.error("An internal error occurred : {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Echec de validation d'un corps annote @Valid.
     *
     * Les controleurs generes recoivent un BindingResult et traitent le cas
     * eux-memes. Ceux ecrits a la main ne le font pas : sans ce gestionnaire,
     * Spring leve MethodArgumentNotValidException, que le filet de securite
     * attrape et transforme en 500 « Une erreur inattendue s'est produite ».
     * Un nom d'evenement vide devenait ainsi une erreur serveur, alors que
     * c'est une saisie incorrecte dont l'application doit pouvoir informer
     * l'utilisateur champ par champ.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {
        Map<String, String> erreurs = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                erreur -> erreurs.put(erreur.getField(), erreur.getDefaultMessage()));

        String message = erreurs.isEmpty()
                ? "Les donnees envoyees sont invalides."
                : String.join(" ; ", erreurs.values());

        RestResponse<Map<String, String>> response =
                RestResponse.buildErrorResponse(HttpStatus.BAD_REQUEST, message, erreurs);
        log.info("Validation failed : {}", erreurs);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RestResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        RestResponse<Void> response = RestResponse.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
        log.error("Illegal argument provided : {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<RestResponse<Void>> handleResponseStatusException(ResponseStatusException ex) {
        String message = ex.getReason() != null ? ex.getReason() : "Une erreur est survenue.";
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        RestResponse<Void> response = RestResponse.buildErrorResponse(status, message, null);
        log.warn("Handled response status exception: status={}, message={}", status, message);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RestResponse<Void>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {
        String message = resolveDuplicateFieldMessage(ex);
        RestResponse<Void> response = RestResponse.buildErrorResponse(HttpStatus.CONFLICT, message, null);
        log.error("Database constraint violation: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<Void>> handleGenericException(Exception ex) {
        RestResponse<Void> response = RestResponse.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur inattendue s'est produite.", null);
        log.error("An unexpected error occurred : {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<RestResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        RestResponse<Void> response = RestResponse.buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
        log.info("Illegal state: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private String resolveDuplicateFieldMessage(DataIntegrityViolationException ex) {
        String rootMessage = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();
        if (rootMessage == null) {
            return "Un compte existe déjà avec ces informations. Veuillez vous connecter ou en utiliser d'autres.";
        }

        String normalized = rootMessage.toLowerCase();
        if (normalized.contains("phone") || normalized.contains("telephone") || normalized.contains("numero")) {
            return "Ce numéro est déjà utilisé. Merci d'en choisir un autre ou de vous connecter..";
        }
        if (normalized.contains("email") || normalized.contains("mail")) {
            return "Cette adresse e-mail est déjà associée à un compte. Veuillez en utiliser une autre ou vous connecter à votre compte existant.";
        }
        return "Un compte existe déjà avec ces informations. Veuillez vous connecter ou en utiliser d'autres.";
    }
}