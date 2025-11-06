package com.mv.api_valentinasvault.service;

import com.mv.api_valentinasvault.model.Streak;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.repository.StreakRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class StreakService {

    private final StreakRepository streakRepository;

    public StreakService(StreakRepository streakRepository) {
        this.streakRepository = streakRepository;
    }

    public void registerActivity(User user) {
        Streak streak = streakRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Streak s = new Streak();
                    s.setUser(user);
                    return s;
                });

        LocalDate today = LocalDate.now();
        if (today.equals(streak.getLastActivityDate())) {
            return;
        }

        if (streak.getLastActivityDate() == null) {
            streak.setCurrentStreak(1);
        } else if (streak.getLastActivityDate().equals(today.minusDays(1))) {
            // Día consecutivo
            streak.setCurrentStreak(streak.getCurrentStreak() + 1);
        } else if (!streak.getLastActivityDate().equals(today)) {
            // Día no consecutivo
            streak.setCurrentStreak(1);
        }

        if (streak.getCurrentStreak() > streak.getBestStreak()) {
            streak.setBestStreak(streak.getCurrentStreak());
        }

        streak.setLastActivityDate(today);
        streakRepository.save(streak);
    }

    public Streak getUserStreak(User user) {
        return streakRepository.findByUserId(user.getId()).orElse(null);
    }
}
