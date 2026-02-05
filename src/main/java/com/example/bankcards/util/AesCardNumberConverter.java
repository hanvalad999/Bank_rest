package com.example.bankcards.util;

import com.example.bankcards.config.CardEncryptionProperties;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * JPA-конвертер, шифрующий и расшифровывающий номера карт с помощью AES/GCM.
 */
@Converter
@Component
public class AesCardNumberConverter implements AttributeConverter<String, String>, ApplicationContextAware {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int IV_LENGTH_BYTES = 12;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            byte[] iv = new byte[IV_LENGTH_BYTES];
            SECURE_RANDOM.nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), spec);
            byte[] cipherText = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));
            ByteBuffer buffer = ByteBuffer.allocate(iv.length + cipherText.length);
            buffer.put(iv);
            buffer.put(cipherText);
            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Unable to encrypt card number", ex);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(dbData);
            ByteBuffer buffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[IV_LENGTH_BYTES];
            buffer.get(iv);
            byte[] cipherText = new byte[buffer.remaining()];
            buffer.get(cipherText);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec);
            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Unable to decrypt card number", ex);
        }
    }

    private SecretKey getSecretKey() {
        if (applicationContext == null) {
            // Fallback для случаев, когда Spring контекст еще не инициализирован
            String encodedKey = System.getProperty("app.card.encryption-key", System.getenv("APP_CARD_ENCRYPTION_KEY"));
            if (encodedKey == null || encodedKey.isBlank()) {
                throw new IllegalStateException("Card encryption key is not configured");
            }
            byte[] keyBytes = Base64.getDecoder().decode(encodedKey);
            return new SecretKeySpec(keyBytes, ALGORITHM);
        }

        CardEncryptionProperties properties = applicationContext.getBean(CardEncryptionProperties.class);
        String encodedKey = properties.getEncryptionKey();
        if (encodedKey == null || encodedKey.isBlank()) {
            throw new IllegalStateException("Card encryption key is not configured. Set app.card.encryption-key property");
        }
        byte[] keyBytes = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}