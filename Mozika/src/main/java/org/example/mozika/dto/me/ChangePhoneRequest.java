package org.example.mozika.dto.me;

import jakarta.validation.constraints.NotBlank;

public class ChangePhoneRequest {

    @NotBlank(message = "Le nouveau numéro de téléphone est obligatoire.")
    private String newPhone;

    public String getNewPhone() { return newPhone; }
    public void setNewPhone(String newPhone) { this.newPhone = newPhone; }
}
