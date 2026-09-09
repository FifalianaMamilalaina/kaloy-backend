package org.example.mozika.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            "mozika-secret-key-a-changer-en-production-1234567890".getBytes()
    );

    private final long expirationMs = 24 * 60 * 60 * 1000; // 24 heures
    private final long otpExpirationMs = 15 * 60 * 1000;   // 15 minutes

    public String generateToken(Long userId, String role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    public String generateOtpToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("purpose", "otp-verification")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + otpExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractUserId(String token) {
        return Long.valueOf(extractClaims(token).getSubject());
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public Long extractUserIdFromOtpToken(String token) {
        Claims claims = extractClaims(token);
        if (!"otp-verification".equals(claims.get("purpose", String.class))) {
            throw new IllegalArgumentException("Token invalide pour cette opération.");
        }
        return Long.valueOf(claims.getSubject());
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}