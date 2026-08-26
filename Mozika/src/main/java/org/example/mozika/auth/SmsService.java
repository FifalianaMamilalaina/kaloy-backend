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

@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    @Value("${mapi.base-url}")
    private String baseUrl;

    @Value("${mapi.sender-id}")
    private String senderId;

    private final MapiTokenService mapiTokenService;
    private final RestTemplate restTemplate;

    public SmsService(MapiTokenService mapiTokenService, RestTemplate restTemplate) {
        this.mapiTokenService = mapiTokenService;
        this.restTemplate = restTemplate;
    }

    public void sendOtpSms(String phoneNumber, String otpCode) {
        String token = mapiTokenService.getValidToken();
        String recipient = phoneNumber.replace("+", "");
        String message = "Votre code de verification Kaloy est : " + otpCode;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("Recipient", recipient);
        body.add("Message", message);
        body.add("Channel", "sms");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/msg/send", request, String.class);
            log.info("SMS OTP envoyé à {} — réponse MAPI : {}", recipient, response.getBody());
        } catch (Exception e) {
            log.error("Erreur envoi SMS MAPI vers {} : {}", recipient, e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Service SMS indisponible");
        }
    }
}
