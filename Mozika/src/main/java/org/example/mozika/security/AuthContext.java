package org.example.mozika.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthContext {

    public Long requireUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new UnauthorizedException("Vous devez être connecté pour effectuer cette action.");
        }
        return userId;
    }

    public String requireRole(HttpServletRequest request, String... allowedRoles) {
        String role = (String) request.getAttribute("userRole");
        if (role == null) {
            throw new UnauthorizedException("Vous devez être connecté pour effectuer cette action.");
        }
        for (String allowed : allowedRoles) {
            if (allowed.equals(role)) {
                return role;
            }
        }
        throw new ForbiddenException("Vous n'avez pas les droits nécessaires pour effectuer cette action.");
    }
}