package com.example.bankcards.entity;

/**
 * Статус запроса на блокировку карты.
 */
public enum BlockRequestStatus {
    /**
     * Запрос ожидает рассмотрения.
     */
    PENDING,

    /**
     * Запрос одобрен.
     */
    APPROVED,

    /**
     * Запрос отклонен.
     */
    REJECTED
}
