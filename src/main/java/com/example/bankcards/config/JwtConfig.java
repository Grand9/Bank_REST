package com.example.bankcards.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class JwtConfig {
    @Value("${security.jwt.uri}")
    private String uri;
    @Value("${security.jwt.header}")
    private String header;
    @Value("${security.jwt.access_secret}")
    private String access_secret;
    @Value("${security.jwt.refresh_secret}")
    private String refresh_secret;
    @Value("${security.jwt.prefix}")
    private String prefix;
    @Value("${security.jwt.access_token_expiration}")
    private int access_expiration;
    @Value("${security.jwt.refresh_token_expiration}")
    private int refresh_expiration;

}