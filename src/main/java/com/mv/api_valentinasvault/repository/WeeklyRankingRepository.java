package com.mv.api_valentinasvault.repository;

import com.mv.api_valentinasvault.model.WeeklyRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WeeklyRankingRepository extends JpaRepository<WeeklyRanking, UUID> {

    List<WeeklyRanking> findByWeekStartOrderByCurrentStreakDesc(LocalDate weekStart);
}
