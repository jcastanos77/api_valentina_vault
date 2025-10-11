package com.mv.api_valentinasvault.repository;

import com.mv.api_valentinasvault.model.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, UUID> {
    List<SavingsGoal> findByUserId(UUID userId);

    List<SavingsGoal> findByUserIdAndIsActiveTrue(UUID userId);

}
