package org.example.mozika.dto.me;

import jakarta.validation.constraints.NotNull;

public class GroupMemberStatusRequest {

    @NotNull(message = "L'identifiant du statut est obligatoire.")
    private Long statusId;

    public Long getStatusId() { return statusId; }
    public void setStatusId(Long statusId) { this.statusId = statusId; }
}
