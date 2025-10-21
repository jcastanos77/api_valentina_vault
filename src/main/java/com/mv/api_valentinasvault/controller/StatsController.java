package com.mv.api_valentinasvault.controller;

import com.mv.api_valentinasvault.model.MonthlySummary;
import com.mv.api_valentinasvault.model.Transaction;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.repository.MonthlySummaryRepository;
import com.mv.api_valentinasvault.repository.TransactionRepository;
import com.mv.api_valentinasvault.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final UserService userService;
    private final MonthlySummaryRepository monthlySummaryRepository;
    private final TransactionRepository transactionRepository;

    @GetMapping("/transactionStats")
    public ResponseEntity<Map<String, Object>> getMonthlyStats(
            @RequestHeader("Authorization") String authHeader
    ) {
        String email = userService.getEmailFromToken(authHeader);
        User user = userService.findByEmail(email);

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        // Buscar resumen mensual (ingresos y gastos totales)
        MonthlySummary summary = monthlySummaryRepository
                .findByUserIdAndYearAndMonth(user.getId(), year, month)
                .orElseThrow(() -> new RuntimeException("No summary found"));

        // Total de ingresos y gastos
        double totalIncome = summary.getTotalIncome().doubleValue();
        double totalExpenses = summary.getBasicosSpent().doubleValue()
                + summary.getAhorroSpent().doubleValue()
                + summary.getLujosSpent().doubleValue();

        // Distribución de gastos
        Map<String, Double> categoryDistribution = new HashMap<>();
        categoryDistribution.put("basicos", summary.getBasicosSpent().doubleValue());
        categoryDistribution.put("ahorro", summary.getAhorroSpent().doubleValue());
        categoryDistribution.put("lujos", summary.getLujosSpent().doubleValue());

        // Últimas transacciones (limitadas a 10)
        List<Transaction> recentTransactions = transactionRepository
                .findTop10ByUserIdOrderByTransactionDateDesc(user.getId());

        List<Map<String, Object>> txList = recentTransactions.stream().map(tx -> {
            Map<String, Object> txMap = new HashMap<>();
            txMap.put("id", tx.getId());
            txMap.put("description", tx.getDescription());
            txMap.put("amount", tx.getAmount());
            txMap.put("category", tx.getCategory());
            txMap.put("type", tx.getType());
            txMap.put("transactionDate", tx.getTransactionDate());
            return txMap;
        }).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("totalIncome", totalIncome);
        result.put("totalExpenses", totalExpenses);
        result.put("categories", categoryDistribution);
        result.put("transactions", txList);

        return ResponseEntity.ok(result);
    }
}
