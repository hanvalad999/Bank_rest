package com.example.bankcards.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки JWT-токенов, загружаемые из конфигурации приложения.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    /**
     * Секретный ключ для подписи JWT (base64).
     */
    private String secret;

    /**
     * Время жизни токена в миллисекундах.
     */
    private long expiration;
}
