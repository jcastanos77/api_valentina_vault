package com.mv.api_valentinasvault.controller;

import com.mv.api_valentinasvault.model.DTOs.GoalRequest;
import com.mv.api_valentinasvault.model.SavingsGoal;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.repository.SavingsGoalRepository;
import com.mv.api_valentinasvault.service.SavingsService;
import com.mv.api_valentinasvault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/savings")
public class SavingsController {

    private final SavingsService savingsService;
    private final UserService userService;
    private final SavingsGoalRepository savingGoalRepository;

    public SavingsController(SavingsService savingsService, UserService userService, SavingsGoalRepository savingGoalRepository) {
        this.savingsService = savingsService;
        this.userService = userService;
        this.savingGoalRepository = savingGoalRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addSavingsGoal(@RequestHeader("Authorization") String authHeader,
                                                              @RequestBody Map<String, Object> body) {
        String email = userService.getEmailFromToken(authHeader);
        String name = (String) body.get("name");
        Double targetAmount = (Double) body.get("targetAmount");

        Map<String, Object> response = savingsService.addSavingsGoal(email, name, targetAmount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/goals")
    public ResponseEntity<?> getGoals(@RequestHeader("Authorization") String authHeader) {
        String email = userService.getEmailFromToken(authHeader);
        User user = userService.findByEmail(email);

        List<SavingsGoal> goals = savingGoalRepository.findByUserId(user.getId());
        List<Map<String, Object>> result = goals.stream().map(goal -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", goal.getId());
            map.put("name", goal.getName());
            map.put("targetAmount", goal.getTargetAmount());
            map.put("currentAmount", goal.getCurrentAmount());
            map.put("progress", goal.getTargetAmount().compareTo(BigDecimal.ZERO) == 0
                    ? 0
                    : goal.getCurrentAmount().divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }


    @GetMapping("/progress")
    public ResponseEntity<List<Map<String, Object>>> getSavingsProgress(@RequestHeader("Authorization") String authHeader) {
        String email = userService.getEmailFromToken(authHeader);
        List<Map<String, Object>> progress = savingsService.getUserSavingsProgressByEmail(email);
        return ResponseEntity.ok(progress);
    }

    @PatchMapping("/{id}/update-amount")
    public ResponseEntity<?> updateAmount(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        BigDecimal newAmount = new BigDecimal(body.get("currentAmount").toString());
        SavingsGoal goal = savingGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        goal.setCurrentAmount(newAmount);
        goal.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(savingGoalRepository.save(goal));
    }
    
}
