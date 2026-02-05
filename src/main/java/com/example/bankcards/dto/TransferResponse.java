package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ответ с информацией о выполненном переводе.
 *
 * @param id        идентификатор перевода
 * @param fromCardId карта-отправитель
 * @param toCardId   карта-получатель
 * @param amount     сумма перевода
 * @param createdAt  дата и время создания перевода
 */
public record TransferResponse(
        Long id,
        Long fromCardId,
        Long toCardId,
        BigDecimal amount,
        LocalDateTime createdAt
    ) {}