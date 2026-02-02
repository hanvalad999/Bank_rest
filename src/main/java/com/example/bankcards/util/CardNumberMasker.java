package com.example.bankcards.util;

public final class CardNumberMasker {

    private CardNumberMasker() {
    }

    public static String mask(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }
}