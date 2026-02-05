package com.example.bankcards.exception;

/**
 * Исключение для ошибок в запросе клиента (HTTP 400).
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
