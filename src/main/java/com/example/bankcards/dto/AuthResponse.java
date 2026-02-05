package com.example.bankcards.dto;

/**
 * Ответ на успешную аутентификацию с JWT-токеном.
 *
 * @param token     значение токена
 * @param tokenType тип токена (обычно Bearer)
 * @param username  имя пользователя
 * @param role      роль пользователя
 */
public record AuthResponse(
        String token,
        String tokenType,
        String username,
        String role
    ) {}
