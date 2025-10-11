package com.mv.api_valentinasvault.repository;

import com.mv.api_valentinasvault.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByUserId(UUID userId);

    List<Transaction> findByUserIdAndTransactionDateBetween(UUID userId, LocalDateTime start, LocalDateTime end);

    List<Transaction> findByUserIdAndCategory(UUID userId, String category);

}
