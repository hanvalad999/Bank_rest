package com.example.bankcards.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @deprecated This class is not used. JWT functionality is handled by JwtService.
 * This file can be removed if not needed.
 */
@Deprecated
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;
}
