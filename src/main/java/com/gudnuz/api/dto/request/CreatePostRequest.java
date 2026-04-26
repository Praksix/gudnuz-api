package com.gudnuz.api.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {

    @NotBlank
    private String titre;

    @NotBlank
    private String contenu;

    @NotBlank
    private String categorie;

    private String source;
}
