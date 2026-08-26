package org.example.mozika.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class MapiTokenService {

    private static final Logger log = LoggerFactory.getLogger(MapiTokenService.class);

    @Value("${mapi.base-url}")
    private String baseUrl;

    @Value("${mapi.username}")
    private String username;

    @Value("${mapi.password}")
    private String password;

    private final RestTemplate restTemplate;

    private String cachedToken;
    private LocalDateTime tokenExpiresAt;

    public MapiTokenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public synchronized String getValidToken() {
        if (cachedToken == null || LocalDateTime.now().isAfter(tokenExpiresAt)) {
            authenticate();
        }
        return cachedToken;
    }

    private void authenticate() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("Username", username);
        body.add("Password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        String loginUrl = baseUrl + "/authentication/login";

        try {
            log.info("Tentative authentification MAPI → {}", loginUrl);
            ResponseEntity<String> rawResponse = restTemplate.postForEntity(loginUrl, request, String.class);

            log.info("Réponse MAPI status={} body={}", rawResponse.getStatusCode(), rawResponse.getBody());

            if (!rawResponse.getStatusCode().is2xxSuccessful() || rawResponse.getBody() == null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "MAPI auth échouée, status=" + rawResponse.getStatusCode());
            }

            // Parser le JSON manuellement pour éviter les problèmes de désérialisation
            String responseBody = rawResponse.getBody();
            String token = extractJsonField(responseBody, "token");
            if (token == null) token = extractJsonField(responseBody, "accessToken");
            if (token == null) token = extractJsonField(responseBody, "access_token");
            if (token == null) token = extractJsonField(responseBody, "data");

            if (token == null) {
                log.error("Token introuvable dans la réponse MAPI : {}", responseBody);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "Service SMS indisponible — token introuvable");
            }

            cachedToken = token;
            tokenExpiresAt = LocalDateTime.now().plusSeconds(900);
            log.info("Token MAPI obtenu, valide jusqu'à {}", tokenExpiresAt);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception authentification MAPI [{}] : {}", e.getClass().getSimpleName(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Service SMS indisponible");
        }
    }

    private String extractJsonField(String json, String fieldName) {
        String key = "\"" + fieldName + "\"";
        int idx = json.indexOf(key);
        if (idx == -1) return null;
        int colon = json.indexOf(':', idx + key.length());
        if (colon == -1) return null;
        int start = json.indexOf('"', colon + 1);
        if (start == -1) return null;
        int end = json.indexOf('"', start + 1);
        if (end == -1) return null;
        return json.substring(start + 1, end);
    }
}
