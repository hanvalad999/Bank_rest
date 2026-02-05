package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Запрос на создание пользователя администратором.
 *
 * @param username имя пользователя
 * @param password пароль
 * @param role     роль пользователя
 */
public record CreateUserRequest(
        @NotBlank @Size(min = 3, max = 100) String username,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotNull Role role
    ) {}