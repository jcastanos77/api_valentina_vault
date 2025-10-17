package com.mv.api_valentinasvault.controller;

import com.mv.api_valentinasvault.model.DTOs.ExpenseRequest;
import com.mv.api_valentinasvault.model.DTOs.IncomeRequest;
import com.mv.api_valentinasvault.model.MonthlySummary;
import com.mv.api_valentinasvault.model.Transaction;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.model.UserRule;
import com.mv.api_valentinasvault.repository.MonthlySummaryRepository;
import com.mv.api_valentinasvault.repository.UserRuleRepository;
import com.mv.api_valentinasvault.service.SavingsService;
import com.mv.api_valentinasvault.service.TransactionService;
import com.mv.api_valentinasvault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;
    private final MonthlySummaryRepository monthlySummaryRepository;
    private final UserRuleRepository userRulesRepository;
    private final SavingsService savingGoalService;
    public TransactionController(TransactionService transactionService, UserService userService, MonthlySummaryRepository monthlySummaryRepository, UserRuleRepository userRulesRepository, SavingsService savingGoalService) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.monthlySummaryRepository = monthlySummaryRepository;
        this.userRulesRepository = userRulesRepository;
        this.savingGoalService = savingGoalService;
    }

    // 🔹 Obtener todas las transacciones de un usuario
    @GetMapping("/user/{userId}")
    public List<Transaction> getTransactionsByUser(@PathVariable UUID userId) {
        return transactionService.getTransactionsByUser(userId);
    }
    @PostMapping("/income")
    public ResponseEntity<String> addIncome(@RequestHeader("Authorization") String authHeader,
                                            @RequestBody IncomeRequest request) {
        String email = userService.getEmailFromToken(authHeader);
        User user = userService.findByEmail(email);
        transactionService.addIncome(
                user,
                BigDecimal.valueOf(request.getAmount()),
                request.getDescription()
        );
        return ResponseEntity.ok("Income added and distributed 50/30/20");
    }
    // 🔹 Crear transacción
    @PostMapping("/expense")
    public ResponseEntity<String> addExpense(@RequestHeader("Authorization") String authHeader,
                                             @RequestBody ExpenseRequest request) {
        String email = userService.getEmailFromToken(authHeader);
        User user = userService.findByEmail(email);

        transactionService.addExpense(
                user,
                request.getCategory(),
                BigDecimal.valueOf(request.getAmount()),
                request.getDescription()
        );

        return ResponseEntity.ok("Expense added to category: " + request.getCategory());
    }

    @GetMapping("/monthlySummary")
    public ResponseEntity<Map<String, Object>> getMonthlySummary(
            @RequestHeader("Authorization") String authHeader) {

        String email = userService.getEmailFromToken(authHeader);
        User user = userService.findByEmail(email);

        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        MonthlySummary summary = monthlySummaryRepository
                .findByUserIdAndYearAndMonth(user.getId(), year, month)
                .orElse(new MonthlySummary(user, year, month));

        Map<String, Object> response = new HashMap<>();
        response.put("totalIncome", summary.getTotalIncome());
        response.put("basicos", summary.getBasicosSpent());
        response.put("ahorro", summary.getAhorroSpent());
        response.put("lujos", summary.getLujosSpent());

        return ResponseEntity.ok(response);
    }

    // 🔹 Eliminar transacción
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/expenses/percentages")
    public ResponseEntity<Map<String, Object>> getExpensesPercentages(@RequestHeader("Authorization") String authHeader) {
        String email = userService.getEmailFromToken(authHeader);
        User user = userService.findByEmail(email);

        MonthlySummary summary = monthlySummaryRepository
                .findByUserIdAndYearAndMonth(user.getId(), LocalDate.now().getYear(), LocalDate.now().getMonthValue())
                .orElseThrow(() -> new RuntimeException("No summary found"));

        // Obtener regla personalizada (si existe)
        UserRule rules = userRulesRepository.findByUserId(user.getId()).orElse(null);

        // Usar valores por defecto si no tiene regla
        int basicosPercent = (rules != null) ? rules.getBasicosPercent() : 50;
        int ahorroPercent  = (rules != null) ? rules.getAhorroPercent()  : 30;
        int lujosPercent   = (rules != null) ? rules.getLujosPercent()   : 20;

        Map<String, Object> result = new HashMap<>();

        // --- BASICOS ---
        double basicosAssigned = summary.getTotalIncome().doubleValue() * (basicosPercent / 100.0);
        double basicosSpent = summary.getBasicosSpent().doubleValue();
        result.put("basicosAssigned", basicosAssigned);
        result.put("basicosSpent", basicosSpent);
        result.put("basicosRemaining", basicosAssigned - basicosSpent);
        result.put("basicosPercentageSpent", (basicosAssigned == 0 ? 0 : (basicosSpent / basicosAssigned) * 100));

        // --- AHORRO ---
        double ahorroAssigned = summary.getTotalIncome().doubleValue() * (ahorroPercent / 100.0);
        double ahorroSpent = summary.getAhorroSpent().doubleValue();
        result.put("ahorroAssigned", ahorroAssigned);
        result.put("ahorroSpent", ahorroSpent);
        result.put("ahorroRemaining", ahorroAssigned - ahorroSpent);
        result.put("ahorroPercentageSpent", (ahorroAssigned == 0 ? 0 : (ahorroSpent / ahorroAssigned) * 100));

        // --- LUJOS ---
        double lujosAssigned = summary.getTotalIncome().doubleValue() * (lujosPercent / 100.0);
        double lujosSpent = summary.getLujosSpent().doubleValue();
        result.put("lujosAssigned", lujosAssigned);
        result.put("lujosSpent", lujosSpent);
        result.put("lujosRemaining", lujosAssigned - lujosSpent);
        result.put("lujosPercentageSpent", (lujosAssigned == 0 ? 0 : (lujosSpent / lujosAssigned) * 100));

        // Agregar los porcentajes configurados
        result.put("basicosPercent", basicosPercent);
        result.put("ahorroPercent", ahorroPercent);
        result.put("lujosPercent", lujosPercent);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/saving")
    public ResponseEntity<String> addDirectSaving(@RequestHeader("Authorization") String authHeader,
                                                  @RequestBody Map<String, BigDecimal> body) {
        String email = userService.getEmailFromToken(authHeader);
        User user = userService.findByEmail(email);
        BigDecimal amount = body.get("amount");

        transactionService.addDirectSaving(user, amount);

        savingGoalService.updateGoalsWithNewSaving(user, amount);

        return ResponseEntity.ok("Direct saving added");
    }

    @GetMapping("/totalSaving")
    public ResponseEntity<Map<String, Object>> getTotalSavings(@RequestHeader("Authorization") String authHeader) {
        String email = userService.getEmailFromToken(authHeader);
        User user = userService.findByEmail(email);

        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        MonthlySummary summary = monthlySummaryRepository
                .findByUserIdAndYearAndMonth(user.getId(), year, month)
                .orElse(new MonthlySummary(user, year, month));

        BigDecimal ahorroDirecto = summary.getAhorroDirecto() != null ? summary.getAhorroDirecto() : BigDecimal.ZERO;
        BigDecimal automaticTransfer = summary.getAutomaticTransfer() != null ? summary.getAutomaticTransfer() : BigDecimal.ZERO;

        Map<String, Object> result = new HashMap<>();
        result.put("ahorroDirecto", ahorroDirecto);
        result.put("automaticTransfer", automaticTransfer);
        result.put("totalAhorro", ahorroDirecto.add(automaticTransfer));

        return ResponseEntity.ok(result);
    }

}
