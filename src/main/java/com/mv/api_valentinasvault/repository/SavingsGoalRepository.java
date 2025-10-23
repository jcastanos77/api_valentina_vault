package com.mv.api_valentinasvault.repository;

import com.mv.api_valentinasvault.model.SavingsGoal;
import com.mv.api_valentinasvault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, UUID> {
    List<SavingsGoal> findByUserId(UUID userId);

    List<SavingsGoal> findByUserIdAndIsActiveTrue(UUID userId);

    Optional<SavingsGoal> findByIdAndUser(UUID id, User user);

}
