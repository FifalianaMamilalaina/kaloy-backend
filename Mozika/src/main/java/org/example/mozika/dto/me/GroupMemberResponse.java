package org.example.mozika.dto.me;

import java.time.LocalDateTime;

public class GroupMemberResponse {
    private Long id;
    private String fullName;
    private Long roleInstrumentId;
    private String roleInstrumentLabel;
    private String photoUrl;
    private String status;
    private LocalDateTime joinedAt;

    public GroupMemberResponse() {}

    public GroupMemberResponse(Long id, String fullName, Long roleInstrumentId,
                               String roleInstrumentLabel, String photoUrl,
                               String status, LocalDateTime joinedAt) {
        this.id = id;
        this.fullName = fullName;
        this.roleInstrumentId = roleInstrumentId;
        this.roleInstrumentLabel = roleInstrumentLabel;
        this.photoUrl = photoUrl;
        this.status = status;
        this.joinedAt = joinedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Long getRoleInstrumentId() { return roleInstrumentId; }
    public void setRoleInstrumentId(Long roleInstrumentId) { this.roleInstrumentId = roleInstrumentId; }
    public String getRoleInstrumentLabel() { return roleInstrumentLabel; }
    public void setRoleInstrumentLabel(String roleInstrumentLabel) { this.roleInstrumentLabel = roleInstrumentLabel; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
