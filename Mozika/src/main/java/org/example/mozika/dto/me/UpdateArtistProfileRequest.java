package org.example.mozika.dto.me;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class UpdateArtistProfileRequest {

    @Size(max = 100, message = "Le nom de scène ne peut pas dépasser 100 caractères.")
    private String stageName;

    private String bio;

    @Min(value = 1900, message = "L'année de début de carrière doit être une année valide (après 1900).")
    @Max(value = 2100, message = "L'année de début de carrière doit être une année valide.")
    private Integer activeSinceYear;

    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Integer getActiveSinceYear() { return activeSinceYear; }
    public void setActiveSinceYear(Integer activeSinceYear) { this.activeSinceYear = activeSinceYear; }
}
