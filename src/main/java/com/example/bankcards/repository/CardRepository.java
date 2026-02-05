package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.Optional;

/**
 * Репозиторий для работы с банковскими картами.
 */
public interface CardRepository extends JpaRepository<Card, Long> {
    /**
     * Возвращает карты пользователя.
     */
    Page<Card> findAllByUserUsername(String username, Pageable pageable);

    /**
     * Возвращает карты пользователя c указанным статусом.
     */
    Page<Card> findAllByUserUsernameAndStatus(String username, CardStatus status, Pageable pageable);

    /**
     * Возвращает все карты с указанным статусом.
     */
    Page<Card> findAllByStatus(CardStatus status, Pageable pageable);

    /**
     * Находит карту по id с установкой блокировки PESSIMISTIC_WRITE
     * для использования в конкурентных операциях (например, при переводах).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Card c where c.id = :id")
    Optional<Card> findByIdForUpdate(@Param("id") Long id);
}