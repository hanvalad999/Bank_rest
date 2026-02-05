package com.example.bankcards.util;

/**
 * Утилита для маскирования номеров банковских карт.
 */
public final class CardNumberMasker {

    private CardNumberMasker() {
    }

    /**
     * Маскирует номер карты, оставляя видимыми только последние 4 цифры.
     *
     * @param cardNumber исходный номер карты
     * @return маскированный номер
     */
    public static String mask(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }
}