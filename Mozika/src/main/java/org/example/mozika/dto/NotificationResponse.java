package org.example.mozika.dto;

import java.time.LocalDateTime;

/**
 * Notification telle que l'application mobile la consomme.
 *
 * L'entite Notification porte des relations vers User, NotificationType et
 * InteractionTarget. La serialiser directement entrainerait tout le graphe de
 * l'utilisateur dans chaque element de la liste — le defaut deja constate sur
 * /songs, dont une page pesait 73 ko pour trente titres.
 *
 * targetType et targetId portent la redirection. Pour une invitation ils
 * valent CONCERT et l'identifiant du concert, ce qui permet a l'ecran
 * d'afficher les boutons Accepter et Refuser et d'appeler
 * PATCH /invitations/{idConcert} sans avoir a deviner la cible.
 */
public class NotificationResponse {

    private Long id;
    private String type;
    private String content;
    private String targetType;
    private Long targetId;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponse(Long id, String type, String content,
                                String targetType, Long targetId,
                                Boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.targetType = targetType;
        this.targetId = targetId;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getTargetType() {
        return targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
