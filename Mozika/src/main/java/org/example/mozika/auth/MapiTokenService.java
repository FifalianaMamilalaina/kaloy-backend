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
        try {
            doLogin();
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            // MAPI code 99 = session encore active (ex : redémarrage serveur sans logout)
            if (msg.contains("encore en cours de session") || msg.contains("\"code\":99") || msg.contains("code\": 99")) {
                log.warn("Session MAPI encore active, tentative de logout puis reconnexion...");
                tryLogout();
                try {
                    doLogin();
                } catch (Exception e2) {
                    log.error("Authentification MAPI échouée après logout : {}", e2.getMessage());
                    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Service SMS indisponible");
                }
            } else {
                log.error("Exception authentification MAPI [{}] : {}", e.getClass().getSimpleName(), msg);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Service SMS indisponible");
            }
        }
    }

    private void doLogin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("Username", username);
        body.add("Password", password);

        String loginUrl = baseUrl + "/authentication/login";
        log.info("Tentative authentification MAPI → {}", loginUrl);

        ResponseEntity<String> rawResponse = restTemplate.postForEntity(
                loginUrl, new HttpEntity<>(body, headers), String.class);

        log.info("Réponse MAPI status={} body={}", rawResponse.getStatusCode(), rawResponse.getBody());

        if (!rawResponse.getStatusCode().is2xxSuccessful() || rawResponse.getBody() == null) {
            throw new RuntimeException("MAPI auth échouée, status=" + rawResponse.getStatusCode()
                    + " body=" + rawResponse.getBody());
        }

        String responseBody = rawResponse.getBody();
        String token = extractJsonField(responseBody, "token");
        if (token == null) token = extractJsonField(responseBody, "accessToken");
        if (token == null) token = extractJsonField(responseBody, "access_token");
        if (token == null) token = extractJsonField(responseBody, "data");

        if (token == null) {
            log.error("Token introuvable dans la réponse MAPI : {}", responseBody);
            throw new RuntimeException("Token MAPI introuvable dans la réponse");
        }

        cachedToken = token;
        // 840s = 14 min, légèrement inférieur aux 900s de MAPI pour éviter les conflits de session
        tokenExpiresAt = LocalDateTime.now().plusSeconds(840);
        log.info("Token MAPI obtenu, valide jusqu'à {}", tokenExpiresAt);
    }

    private void tryLogout() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            if (cachedToken != null) {
                headers.setBearerAuth(cachedToken);
            }
            String logoutUrl = baseUrl + "/authentication/logout";
            log.info("Tentative logout MAPI → {}", logoutUrl);
            restTemplate.postForEntity(logoutUrl,
                    new HttpEntity<>(new LinkedMultiValueMap<>(), headers), String.class);
            log.info("Logout MAPI effectué");
        } catch (Exception e) {
            log.warn("Logout MAPI échoué (ignoré) : {}", e.getMessage());
        } finally {
            cachedToken = null;
            tokenExpiresAt = null;
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
