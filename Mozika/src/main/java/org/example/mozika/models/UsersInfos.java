package org.example.mozika.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users_infos")
public class UsersInfos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name")
    @Size(max = 255)
    private String name;

    @Column(name = "last_name")
    @Size(max = 255)
    private String lastName;

    @Column(name = "user_name", unique = true)
    @Size(max = 255)
    private String userName;

    @ManyToOne
    @JoinColumn(name = "id_user")
    @NotNull(message = "User cannot be null")
    private User user;
}
