package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Запрос на обновление статуса карты.
 *
 * @param status новый статус
 */
public record CardStatusUpdateRequest(
        @NotNull CardStatus status
    ) {}
