package com.example.bankcards.repository;

import com.example.bankcards.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с переводами.
 */
public interface TransferRepository extends JpaRepository<Transfer, Long> {
}