package org.example.mozika.dto.me;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ChangeEmailRequest {

    @NotBlank(message = "Le nouvel email est obligatoire.")
    @Email(message = "Format d'email invalide.")
    private String newEmail;

    public String getNewEmail() { return newEmail; }
    public void setNewEmail(String newEmail) { this.newEmail = newEmail; }
}
