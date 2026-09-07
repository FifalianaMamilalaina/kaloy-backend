package org.example.mozika.dto.me;

import jakarta.validation.constraints.Size;

public class UpdatePersonalInfoRequest {

    @Size(max = 255, message = "Le prénom ne peut pas dépasser 255 caractères.")
    private String firstName;

    @Size(max = 255, message = "Le nom ne peut pas dépasser 255 caractères.")
    private String lastName;

    @Size(max = 255, message = "Le nom d'utilisateur ne peut pas dépasser 255 caractères.")
    private String userName;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
