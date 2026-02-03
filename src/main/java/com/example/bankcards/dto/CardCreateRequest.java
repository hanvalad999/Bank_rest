package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardCreateRequest(
        @NotNull Long userId,
        @NotBlank @Size(min = 12, max = 19) String cardNumber,
        @NotNull LocalDate expirationDate,
        @NotNull @PositiveOrZero BigDecimal initialBalance
    ) {}