package org.example.mozika.dto.me;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AddGroupMemberRequest {

    @NotBlank(message = "Le nom complet du membre est obligatoire.")
    @Size(max = 100, message = "Le nom complet ne peut pas dépasser 100 caractères.")
    private String fullName;

    @NotNull(message = "Le rôle/instrument est obligatoire.")
    private Long roleInstrumentId;

    private String photoUrl;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Long getRoleInstrumentId() { return roleInstrumentId; }
    public void setRoleInstrumentId(Long roleInstrumentId) { this.roleInstrumentId = roleInstrumentId; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
