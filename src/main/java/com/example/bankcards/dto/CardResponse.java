package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardResponse(
        Long id,
        Long userId,
        String owner,
        String maskedNumber,
        BigDecimal balance,
        String status,
        LocalDate expirationDate
    ) {}