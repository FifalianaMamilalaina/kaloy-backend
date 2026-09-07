package org.example.mozika.dto.me;

import jakarta.validation.constraints.NotBlank;

public class UpdatePhotoRequest {

    @NotBlank(message = "L'URL de la photo est obligatoire.")
    private String photoUrl;

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
