package org.example.mozika.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.mozika.models.User;

@Getter
@Setter
public class UsersInfosSearch {

    private Long id;
    private String name;
    private String lastName;
    private String userName;
    private User user;
}
