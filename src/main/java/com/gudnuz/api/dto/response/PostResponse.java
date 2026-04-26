package com.gudnuz.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private UUID id;
    private String titre;
    private String contenu;
    private String source;
    private String imageUrl;
    private String categorie;
    private int sparksCount;
    private String pseudo;
    private LocalDateTime createdAt;
}
