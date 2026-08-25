package org.example.mozika.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterArtistRequest {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    @NotBlank(message = "Le type d'artiste est obligatoire (SOLO ou GROUP)")
    private String artistType;

    @NotBlank(message = "Le nom de scène est obligatoire")
    private String stageName;

    private Integer activeSinceYear;
    private String bio;
    private String photoUrl;
}
