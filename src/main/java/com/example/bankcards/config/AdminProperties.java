package com.example.bankcards.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки учетной записи администратора, создаваемой при старте приложения.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.admin")
public class AdminProperties {
    /**
     * Имя пользователя администратора.
     */
    private String username;

    /**
     * Пароль администратора в открытом виде, будет захеширован при создании.
     */
    private String password;
}