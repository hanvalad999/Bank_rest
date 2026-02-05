package com.example.bankcards.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки шифрования номеров карт.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.card")
public class CardEncryptionProperties {
    /**
     * Base64‑кодированный 32-байтный ключ для AES-шифрования номеров карт.
     */
    private String encryptionKey;
}

