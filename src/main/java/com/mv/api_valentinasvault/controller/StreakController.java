package com.mv.api_valentinasvault.controller;

import com.mv.api_valentinasvault.model.Streak;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.repository.StreakRepository;
import com.mv.api_valentinasvault.repository.UserRepository;
import com.mv.api_valentinasvault.service.StreakService;
import com.mv.api_valentinasvault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@RestController
@RequestMapping("/api/streak")
public class StreakController {

    private final StreakService streakService;
    private final UserService userService;
    private UserRepository userRepository;
    private StreakRepository streakRepository;

    public StreakController(StreakService streakService, UserService userService) {
        this.streakService = streakService;
        this.userService = userService;
    }

    @GetMapping("/userStreak")
    public ResponseEntity<?> getUserStreak(@RequestHeader("Authorization") String authHeader) {
        String email = userService.getEmailFromToken(authHeader);
        User user = userService.findByEmail(email);
        Streak streak = streakService.getUserStreak(user);

        if (streak == null) {
            return ResponseEntity.ok(Map.of("currentStreak", 0, "bestStreak", 0));
        }

        return ResponseEntity.ok(Map.of(
                "currentStreak", streak.getCurrentStreak(),
                "bestStreak", streak.getBestStreak(),
                "lastActivity", streak.getLastActivityDate()
        ));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<Map<String, Object>>> getWeeklyRanking() {
        List<User> users = userRepository.findAll();

        List<Map<String, Object>> ranking = users.stream()
                .map(u -> {
                    Streak s = streakRepository.findByUserId(u.getId()).orElse(null);
                    Map<String, Object> map = new HashMap<>();
                    map.put("userName", u.getFullName());
                    map.put("currentStreak", s != null ? s.getCurrentStreak() : 0);
                    map.put("bestStreak", s != null ? s.getBestStreak() : 0);
                    return map;
                })
                .sorted((a, b) -> ((Integer) b.get("currentStreak"))
                        .compareTo((Integer) a.get("currentStreak")))
                .limit(10) // top 10
                .toList();

        return ResponseEntity.ok(ranking);
    }

}
