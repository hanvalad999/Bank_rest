package com.example.bankcards.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Стандартная структура ответа об ошибке REST API.
 *
 * @param timestamp момент возникновения ошибки
 * @param status    HTTP-статус
 * @param error     краткое описание статуса
 * @param message   основное сообщение об ошибке
 * @param details   дополнительные детали (например, ошибки валидации)
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        List<String> details
    ) {
}