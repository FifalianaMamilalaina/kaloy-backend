package org.example.mozika.config;

import org.example.mozika.security.AuthContext;
import org.example.mozika.security.RoleRequiredInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Set;

@Configuration
public class RouteSecurityConfig implements WebMvcConfigurer {

    private final AuthContext authContext;

    public RouteSecurityConfig(AuthContext authContext) {
        this.authContext = authContext;
    }

    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "DELETE");

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // Utilisateurs : réservé aux administrateurs (contient des données sensibles)
        registry.addInterceptor(new RoleRequiredInterceptor(authContext, null, "ADMIN"))
                .addPathPatterns("/users/**");

        // Clients : réservé aux administrateurs
        registry.addInterceptor(new RoleRequiredInterceptor(authContext, null, "ADMIN"))
                .addPathPatterns("/clients/**");

        // Artistes : consultation publique (cohérent avec le cahier des charges),
        // modification réservée aux administrateurs
        registry.addInterceptor(new RoleRequiredInterceptor(authContext, WRITE_METHODS, "ADMIN"))
                .addPathPatterns("/artists/**");

        // Membres de groupe : consultation publique, modification réservée aux administrateurs
        registry.addInterceptor(new RoleRequiredInterceptor(authContext, WRITE_METHODS, "ADMIN"))
                .addPathPatterns("/artistgroupmembers/**");

        // Codes de vérification OTP : jamais accessibles via cette API générique
        registry.addInterceptor(new RoleRequiredInterceptor(authContext, null, "ADMIN"))
                .addPathPatterns("/verificationcodes/**");

        // Follows : réservé aux utilisateurs connectés
        registry.addInterceptor(new RoleRequiredInterceptor(authContext, null, "CLIENT", "ARTIST", "ADMIN"))
                .addPathPatterns("/follows/**");

        // Notifications et préférences : réservé aux utilisateurs connectés
        registry.addInterceptor(new RoleRequiredInterceptor(authContext, null, "CLIENT", "ARTIST", "ADMIN"))
                .addPathPatterns("/notifications/**", "/notificationpreferences/**");

        // Soumissions de contenu (pipeline .zip) : réservé aux artistes et administrateurs
        registry.addInterceptor(new RoleRequiredInterceptor(authContext, null, "ARTIST", "ADMIN"))
                .addPathPatterns("/contentsubmissions/**");
    }
}