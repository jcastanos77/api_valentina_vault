package com.mv.api_valentinasvault.repository;

import com.mv.api_valentinasvault.model.MonthlySummary;
import com.mv.api_valentinasvault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MonthlySummaryRepository extends JpaRepository<MonthlySummary, UUID> {
    Optional<MonthlySummary> findByUserIdAndYearAndMonth(UUID userId, int year, int month);

    Optional<MonthlySummary> findByUserAndYearAndMonth(User user, int year, int month);

}
