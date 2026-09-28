package com.gudnuz.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopUserResponse {
    private UUID id;
    private String pseudo;
    private String avatarUrl;
    private Long sparksCount;   // Long, pas int : COUNT() renvoie toujours un Long en JPQL
}