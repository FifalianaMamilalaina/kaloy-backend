package org.example.mozika.dto;

public record ChangeContactRequest(
        String newEmail,
        String newPhone
) {
}