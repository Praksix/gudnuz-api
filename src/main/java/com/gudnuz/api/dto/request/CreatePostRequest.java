package com.gudnuz.api.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(max = 5000, message = "Le contenu ne peut pas dépasser 5000 caractères")
    private String contenu;

    @NotBlank
    private String categorie;

    private String source;
}
