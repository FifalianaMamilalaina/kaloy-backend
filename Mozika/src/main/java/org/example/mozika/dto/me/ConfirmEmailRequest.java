package org.example.mozika.dto.me;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ConfirmEmailRequest {

    @NotBlank(message = "Le code OTP est obligatoire.")
    @Size(min = 6, max = 6, message = "Le code OTP doit comporter exactement 6 chiffres.")
    private String code;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
