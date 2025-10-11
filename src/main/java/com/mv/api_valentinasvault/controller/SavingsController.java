package com.mv.api_valentinasvault.controller;

import com.mv.api_valentinasvault.model.DTOs.GoalRequest;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.service.SavingsService;
import com.mv.api_valentinasvault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/savings")
public class SavingsController {

    private final SavingsService savingsService;
    private final UserService userService;

    public SavingsController(SavingsService savingsService, UserService userService) {
        this.savingsService = savingsService;
        this.userService = userService;
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


    @GetMapping("/progress")
    public ResponseEntity<List<Map<String, Object>>> getSavingsProgress(@RequestHeader("Authorization") String authHeader) {
        String email = userService.getEmailFromToken(authHeader);
        List<Map<String, Object>> progress = savingsService.getUserSavingsProgressByEmail(email);
        return ResponseEntity.ok(progress);
    }
    
}
