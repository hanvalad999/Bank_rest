package com.example.bankcards.dto;

/**
 * Ответ с информацией о пользователе.
 *
 * @param id       идентификатор пользователя
 * @param username имя пользователя
 * @param role     роль пользователя
 */
public record UserResponse(
        Long id,
        String username,
        String role
    ) {}