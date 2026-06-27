package com.newjirasystem.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        int expirationInMs,
        int refreshExpirationInMs
) {}
