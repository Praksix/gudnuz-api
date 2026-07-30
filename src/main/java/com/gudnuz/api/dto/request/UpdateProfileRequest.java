package com.gudnuz.api.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {


    @Size(min = 8)
    private String newPassword;


    @Size(max = 200)
    private String bio;

    private String currentPassword;
}
