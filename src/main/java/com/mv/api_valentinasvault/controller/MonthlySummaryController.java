package com.mv.api_valentinasvault.controller;

import com.mv.api_valentinasvault.model.MonthlySummary;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.service.MonthlyJobService;
import com.mv.api_valentinasvault.service.MonthlySummaryService;
import com.mv.api_valentinasvault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/monthly-summary")
public class MonthlySummaryController {

    private final MonthlySummaryService monthlySummaryService;
    private final UserService userService;

    private final MonthlyJobService monthlyJobService;

    public MonthlySummaryController(MonthlySummaryService monthlySummaryService, UserService userService, MonthlyJobService monthlyJobService) {
        this.monthlySummaryService = monthlySummaryService;
        this.userService = userService;
        this.monthlyJobService = monthlyJobService;
    }

    @GetMapping("/{year}/{month}")
    public ResponseEntity<MonthlySummary> getMonthlySummary(@RequestHeader("Authorization") String authHeader,
                                                            @PathVariable int year,
                                                            @PathVariable int month) {
        String email = userService.getEmailFromToken(authHeader);
        User user = userService.findByEmail(email);
        MonthlySummary summary = monthlySummaryService.getMonthlySummary(user, year, month);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/close")
    public ResponseEntity<String> closePreviousMonth() {
        monthlyJobService.closePreviousMonth();
        return ResponseEntity.ok("✅ Cierre de mes ejecutado manualmente");
    }
}
