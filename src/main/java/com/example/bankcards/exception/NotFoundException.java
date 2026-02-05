package com.example.bankcards.exception;

/**
 * Исключение для ситуаций, когда ресурс не найден (HTTP 404).
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
