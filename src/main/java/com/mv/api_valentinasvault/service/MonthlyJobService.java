package com.mv.api_valentinasvault.service;

import com.mv.api_valentinasvault.model.Streak;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.model.WeeklyRanking;
import com.mv.api_valentinasvault.repository.SavingsGoalRepository;
import com.mv.api_valentinasvault.repository.StreakRepository;
import com.mv.api_valentinasvault.repository.UserRepository;
import com.mv.api_valentinasvault.repository.WeeklyRankingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MonthlyJobService {

    @Autowired
    private UserRepository userRepository;

    private NotificationService notificationService;
    private StreakRepository streakRepository;
    private WeeklyRankingRepository weeklyRankingRepository;

    @Scheduled(cron = "0 0 8 1,15 * *") // Cada quincena a las 8:00 AM
    public void scheduleResetReminders() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            notificationService.createNotification(
                    user,
                    "¿Quieres reiniciar tus resúmenes?",
                    "Ha comenzado una nueva quincena. Puedes reiniciar tus resúmenes financieros si lo deseas.",
                    "RESET_SUMMARY_REMINDER"
            );
        }

        System.out.println("🔔 Recordatorios quincenales generados para " + users.size() + " usuarios");
    }

    @Scheduled(cron = "0 0 8 * * MON", zone = "America/Mexico_City")
    public void generateWeeklyRanking() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(java.time.DayOfWeek.MONDAY); // inicio de la semana

        List<User> users = userRepository.findAll();

        List<WeeklyRanking> ranking = users.stream()
                .map(u -> {
                    Streak s = streakRepository.findByUserId(u.getId()).orElse(null);
                    WeeklyRanking r = new WeeklyRanking();
                    r.setUser(u);
                    r.setCurrentStreak(s != null ? s.getCurrentStreak() : 0);
                    r.setBestStreak(s != null ? s.getBestStreak() : 0);
                    r.setWeekStart(monday);
                    return r;
                })
                .sorted((a, b) -> Integer.compare(b.getCurrentStreak(), a.getCurrentStreak()))
                .limit(10)
                .toList();

        weeklyRankingRepository.saveAll(ranking);

        System.out.println("🏆 Ranking semanal generado con " + ranking.size() + " usuarios.");
    }



}
