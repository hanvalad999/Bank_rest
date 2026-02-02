package com.example.bankcards.dto;

public record UserResponse(
        Long id,
        String username,
        String role
    ) {}