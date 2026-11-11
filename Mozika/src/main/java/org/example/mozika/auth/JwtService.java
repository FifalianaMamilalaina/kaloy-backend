package org.example.mozika.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.Keys;
import org.example.mozika.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

@Service
public class JwtService {

    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    @Value("${app.jwt.refresh-expiration-ms:2592000000}")
    private long refreshExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        String role = user.getRoleidUserRoles() != null ? user.getRoleidUserRoles().getName() : "CLIENT";
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", role)
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken() {
        byte[] tokenBytes = new byte[32];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    public String hashRefreshToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    public java.time.LocalDateTime refreshTokenExpiresAt() {
        return java.time.LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000L);
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        return validateAccessToken(token) == TokenValidationResult.VALID;
    }

    public TokenValidationResult validateAccessToken(String token) {
        try {
            Claims claims = parseClaims(token);
            if (claims.getExpiration() == null || !claims.getExpiration().after(new Date())) {
                return TokenValidationResult.EXPIRED;
            }
            String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);
            return tokenType == null || ACCESS_TOKEN_TYPE.equals(tokenType)
                    ? TokenValidationResult.VALID
                    : TokenValidationResult.WRONG_TYPE;
        } catch (ExpiredJwtException e) {
            return TokenValidationResult.EXPIRED;
        } catch (SignatureException e) {
            return TokenValidationResult.INVALID_SIGNATURE;
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return TokenValidationResult.MALFORMED;
        } catch (JwtException e) {
            return TokenValidationResult.INVALID;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public enum TokenValidationResult {
        VALID,
        EXPIRED,
        INVALID_SIGNATURE,
        MALFORMED,
        WRONG_TYPE,
        INVALID
    }
}
