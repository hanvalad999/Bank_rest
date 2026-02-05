package com.example.bankcards.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Утилитарные методы для работы с текущим аутентифицированным пользователем.
 */
public final class SecurityUtil {

    private SecurityUtil() {
    }

    /**
     * Возвращает имя текущего аутентифицированного пользователя.
     *
     * @return имя пользователя или null, если пользователь не аутентифицирован
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    /**
     * Проверяет, имеет ли текущий пользователь указанную роль.
     *
     * @param role роль без префикса ROLE_
     * @return true, если роль присутствует
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String expected = "ROLE_" + role;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (expected.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}