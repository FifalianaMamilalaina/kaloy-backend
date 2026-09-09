package org.example.mozika.dto;

public record OtpValidateRequest(
        String otpToken,
        String code
) {
}