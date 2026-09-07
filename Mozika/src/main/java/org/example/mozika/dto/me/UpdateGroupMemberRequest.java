package org.example.mozika.dto.me;

import jakarta.validation.constraints.Size;

public class UpdateGroupMemberRequest {

    @Size(max = 100, message = "Le nom complet ne peut pas dépasser 100 caractères.")
    private String fullName;

    private Long roleInstrumentId;

    private String photoUrl;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Long getRoleInstrumentId() { return roleInstrumentId; }
    public void setRoleInstrumentId(Long roleInstrumentId) { this.roleInstrumentId = roleInstrumentId; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
