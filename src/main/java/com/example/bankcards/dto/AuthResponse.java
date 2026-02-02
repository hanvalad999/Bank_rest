package com.example.bankcards.dto;

public record AuthResponse(
        String token,
        String tokenType,
        String username,
        String role
    ) {}
