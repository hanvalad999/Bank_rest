package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Точка входа в приложение управления банковскими картами.
 */
@SpringBootApplication
@EnableRetry
public class BankcardsApplication {
    public BankcardsApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(BankcardsApplication.class, args);
    }
}