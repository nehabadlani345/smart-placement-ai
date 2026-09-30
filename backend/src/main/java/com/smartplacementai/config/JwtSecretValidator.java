package com.smartplacementai.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class JwtSecretValidator {

    @Value("${app.jwt.secret}")
    private String secret;

    @PostConstruct
    public void validate() {
        if (secret == null || secret.isBlank()
                || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "app.jwt.secret must be set and at least 32 bytes (256 bits) for HS256. Check JWT_SECRET.");
        }
        if (secret.startsWith("replace-with")) {
            throw new IllegalStateException(
                    "JWT_SECRET is still the placeholder value from .env.example. Generate a real one.");
        }
    }
}
