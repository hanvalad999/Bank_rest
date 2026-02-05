package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Запрос на перевод средств между картами.
 *
 * @param fromCardId идентификатор карты-отправителя
 * @param toCardId   идентификатор карты-получателя
 * @param amount     сумма перевода
 */
public record TransferRequest(
        @NotNull Long fromCardId,
        @NotNull Long toCardId,
        @NotNull @Positive BigDecimal amount
    ) {}