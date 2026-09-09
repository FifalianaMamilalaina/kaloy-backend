package org.example.mozika.dto;

public record RegisterRequest(
        String email,
        String phone,
        String password,
        String role,      
        String artistType,  
        String stageName    
) {
}