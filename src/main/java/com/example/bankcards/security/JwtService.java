package com.example.bankcards.security;

import com.example.bankcards.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

/**
 * Сервис для генерации и валидации JWT-токенов.
 */
@Service
public class JwtService {

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    /**
     * Генерирует новый JWT-токен для указанного пользователя.
     *
     * @param username имя пользователя
     * @param role     роль пользователя
     * @return строковое представление JWT
     */
    public String generateToken(String username, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(properties.getExpiration())))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Проверяет подпись и срок действия токена.
     *
     * @param token JWT
     * @return true, если токен валиден
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Извлекает имя пользователя из токена.
     */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Извлекает роль пользователя из токена.
     */
    public String extractRole(String token) {
        Object role = getClaims(token).get("role");
        return role == null ? null : role.toString();
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        String secret = properties.getSecret();
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
