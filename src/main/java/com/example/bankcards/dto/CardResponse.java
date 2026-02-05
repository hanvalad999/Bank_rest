package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Ответ с информацией о банковской карте.
 *
 * @param id             идентификатор карты
 * @param userId         идентификатор пользователя-владельца
 * @param owner          имя владельца
 * @param maskedNumber   маскированный номер карты
 * @param balance        текущий баланс
 * @param status         статус карты
 * @param expirationDate дата истечения срока действия
 */
public record CardResponse(
        Long id,
        Long userId,
        String owner,
        String maskedNumber,
        BigDecimal balance,
        String status,
        LocalDate expirationDate
    ) {}