package org.example.mozika.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

public class RoleRequiredInterceptor implements HandlerInterceptor {

    private final AuthContext authContext;
    private final Set<String> protectedMethods; // null = toutes les méthodes HTTP
    private final String[] allowedRoles;

    public RoleRequiredInterceptor(AuthContext authContext, Set<String> protectedMethods, String... allowedRoles) {
        this.authContext = authContext;
        this.protectedMethods = protectedMethods;
        this.allowedRoles = allowedRoles;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (protectedMethods == null || protectedMethods.contains(request.getMethod())) {
            authContext.requireRole(request, allowedRoles);
        }
        return true;
    }
}