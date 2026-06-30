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
public class UserResponse {

    private UUID id;
    private String pseudo;
    private String bio;
    private String email;
    private String role;
    private String userLevel;
    private LocalDateTime createdAt;
    private int postsCount;
    private int sparksCount;
    private int flaresCount;
}
