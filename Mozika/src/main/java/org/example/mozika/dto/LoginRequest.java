package org.example.mozika.dto;

public record LoginRequest(
        String email,
        String phone,
        String password
) {
}