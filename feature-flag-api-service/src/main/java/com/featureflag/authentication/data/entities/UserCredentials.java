package com.featureflag.authentication.data.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserCredentials {
    @Schema(type = "string", example = "test_user")
    private String username;
    @Schema(type = "string", example = "password123")
    private String password;
}
