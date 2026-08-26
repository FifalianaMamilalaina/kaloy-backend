package org.example.mozika.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendOtpRequest {

    @NotNull(message = "L'identifiant utilisateur est obligatoire")
    private Long userId;
}
