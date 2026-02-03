package com.example.bankcards.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationValidator {

    private final JwtProperties jwtProperties;
    private final CardEncryptionProperties cardEncryptionProperties;

    public ConfigurationValidator(JwtProperties jwtProperties, CardEncryptionProperties cardEncryptionProperties) {
        this.jwtProperties = jwtProperties;
        this.cardEncryptionProperties = cardEncryptionProperties;
    }

    @PostConstruct
    public void validateConfiguration() {
        if (jwtProperties.getSecret() == null || jwtProperties.getSecret().isBlank()) {
            throw new IllegalStateException(
                    "JWT_SECRET environment variable or system property is required. " +
                            "Set it before starting the application."
            );
        }
        if (jwtProperties.getSecret().length() < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET must be at least 32 characters long. " +
                            "Current length: " + jwtProperties.getSecret().length()
            );
        }

        if (cardEncryptionProperties.getEncryptionKey() == null || cardEncryptionProperties.getEncryptionKey().isBlank()) {
            throw new IllegalStateException(
                    "APP_CARD_ENCRYPTION_KEY environment variable or system property is required. " +
                            "Set it before starting the application."
            );
        }

        try {
            byte[] keyBytes = java.util.Base64.getDecoder().decode(cardEncryptionProperties.getEncryptionKey());
            if (keyBytes.length != 32) {
                throw new IllegalStateException(
                        "APP_CARD_ENCRYPTION_KEY must be a base64-encoded 32-byte key. " +
                                "Current decoded length: " + keyBytes.length + " bytes"
                );
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "APP_CARD_ENCRYPTION_KEY must be a valid base64-encoded string: " + e.getMessage()
            );
        }
    }
}

