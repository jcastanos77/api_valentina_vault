package com.mv.api_valentinasvault.controller;

import com.mv.api_valentinasvault.model.MonthlySummary;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.service.MonthlyJobService;
import com.mv.api_valentinasvault.service.MonthlySummaryService;
import com.mv.api_valentinasvault.service.TransactionService;
import com.mv.api_valentinasvault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/monthly-summary")
public class MonthlySummaryController {

    private final MonthlySummaryService monthlySummaryService;
    private final UserService userService;

    private final MonthlyJobService monthlyJobService;

    private final TransactionService transactionService;

    public MonthlySummaryController(MonthlySummaryService monthlySummaryService, UserService userService, MonthlyJobService monthlyJobService, TransactionService transactionService) {
        this.monthlySummaryService = monthlySummaryService;
        this.userService = userService;
        this.monthlyJobService = monthlyJobService;
        this.transactionService = transactionService;
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

    @PostMapping("/resetSummaries")
    public ResponseEntity<String> resetSummaries(@RequestHeader("Authorization") String authHeader) {
        String email = userService.getEmailFromToken(authHeader);
        User user = userService.findByEmail(email);
        LocalDate now = LocalDate.now();

        int lastMonth = now.getMonthValue();
        int year = now.getYear();

        transactionService.closeMonth(user, year, lastMonth);

        return ResponseEntity.ok("Resúmenes reiniciados correctamente");
    }

}
