package com.featureflag.authentication.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "jwt", ignoreUnknownFields = false)
public class JwtProperties {
    private String secret;
    private long tokenValidityInSeconds = 1800;
    private String admin;
    private String password;
}
