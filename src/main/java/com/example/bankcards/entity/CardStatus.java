package com.example.bankcards.entity;

/**
 * Статус банковской карты.
 */
public enum CardStatus {
    /**
     * Активная карта.
     */
    ACTIVE,

    /**
     * Заблокированная карта.
     */
    BLOCKED,

    /**
     * Истекшая карта.
     */
    EXPIRED
}
