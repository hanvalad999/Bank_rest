package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос на аутентификацию пользователя.
 *
 * @param username имя пользователя
 * @param password пароль
 */
public record AuthRequest(
        @NotBlank String username,
        @NotBlank String password
    ) {}