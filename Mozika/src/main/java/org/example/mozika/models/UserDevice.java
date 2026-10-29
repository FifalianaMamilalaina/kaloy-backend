package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Appareil sur lequel un utilisateur recoit les notifications push.
 *
 * Le jeton est unique a lui seul, pas par utilisateur : un meme telephone sert
 * a plusieurs comptes, et a la reconnexion le jeton doit changer de
 * proprietaire plutot que de se dupliquer.
 */
@Setter
@Getter
@Entity
@Table(name = "user_devices")
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "UserId cannot be null")
    private User useridUsers;

    @Column(name = "token", unique = true, nullable = false, length = 512)
    @NotNull(message = "Token cannot be null")
    private String token;

    /** ANDROID ou IOS. */
    @Column(name = "platform", nullable = false, length = 16)
    @NotNull(message = "Platform cannot be null")
    private String platform;

    @Column(name = "created_at", nullable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "last_seen_at", nullable = false)
    private java.time.LocalDateTime lastSeenAt;
}
