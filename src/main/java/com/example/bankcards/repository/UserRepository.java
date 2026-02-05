package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Ищет пользователя по имени.
     *
     * @param username имя пользователя
     * @return найденный пользователь или пустой Optional
     */
    Optional<User> findByUsername(String username);

    /**
     * Проверяет существование пользователя с указанным именем.
     *
     * @param username имя пользователя
     * @return true, если пользователь существует
     */
    boolean existsByUsername(String username);
}