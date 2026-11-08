package org.example.mozika.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowArtisteResponse {
    private Long followId;
    private Long artisteId;
    private String stageName;
    private String photoUrl;
}
