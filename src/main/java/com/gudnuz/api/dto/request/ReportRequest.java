package com.gudnuz.api.dto.request;

import com.gudnuz.api.model.Post;
import com.gudnuz.api.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {

    private User user;

    private Post post;

    @NotBlank
    private String motif;

}


