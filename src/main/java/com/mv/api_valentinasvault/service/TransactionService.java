package com.mv.api_valentinasvault.service;

import com.mv.api_valentinasvault.model.MonthlySummary;
import com.mv.api_valentinasvault.model.Transaction;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.model.UserRule;
import com.mv.api_valentinasvault.repository.MonthlySummaryRepository;
import com.mv.api_valentinasvault.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final MonthlySummaryRepository monthlySummaryRepository;

    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository, MonthlySummaryRepository monthlySummaryRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.monthlySummaryRepository = monthlySummaryRepository;
        this.userService = userService;
    }

    public List<Transaction> getTransactionsByUser(UUID userId) {
        return transactionRepository.findByUserId(userId);
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(UUID id) {
        transactionRepository.deleteById(id);
    }


    public void addIncome(User user, BigDecimal amount, String description) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        MonthlySummary summary = monthlySummaryRepository.findByUserAndYearAndMonth(user, year, month)
                .orElseGet(() -> {
                    MonthlySummary summaryInside = new MonthlySummary();
                    summaryInside.setUser(user);
                    summaryInside.setYear(year);
                    summaryInside.setMonth(month);
                    summaryInside.setTotalIncome(java.math.BigDecimal.ZERO);
                    summaryInside.setBasicosSpent(java.math.BigDecimal.ZERO);
                    summaryInside.setAhorroSpent(java.math.BigDecimal.ZERO);
                    summaryInside.setLujosSpent(java.math.BigDecimal.ZERO);
                    return summaryInside;
                });

        if (summary.getTotalIncome() == null) summary.setTotalIncome(BigDecimal.ZERO);
        if (summary.getBasicosSpent() == null) summary.setBasicosSpent(BigDecimal.ZERO);
        if (summary.getAhorroSpent() == null) summary.setAhorroSpent(BigDecimal.ZERO);
        if (summary.getLujosSpent() == null) summary.setLujosSpent(BigDecimal.ZERO);

        UserRule userRules = userService.getRulesByEmail(user.getEmail());

        BigDecimal basicos = amount.multiply(BigDecimal.valueOf(userRules.getBasicosPercent()/100));
        BigDecimal ahorro = amount.multiply(BigDecimal.valueOf(userRules.getAhorroPercent()/100));
        BigDecimal lujos = amount.multiply(BigDecimal.valueOf(userRules.getLujosPercent()/100));

        summary.setTotalIncome(summary.getTotalIncome().add(amount));
        summary.setBasicosSpent(basicos);
        summary.setAhorroSpent(ahorro);
        summary.setLujosSpent(lujos);

        monthlySummaryRepository.save(summary);

        // Guardamos las 3 transacciones
        transactionRepository.save(new Transaction(user, "income", "basicos", basicos, description, now));
        transactionRepository.save(new Transaction(user, "income", "ahorro", ahorro, description, now));
        transactionRepository.save(new Transaction(user, "income", "lujos", lujos, description, now));
    }

    public Transaction addExpense(User user, String category, BigDecimal amount, String description) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        MonthlySummary summary = monthlySummaryRepository
                .findByUserIdAndYearAndMonth(user.getId(), year, month)
                .orElseThrow(() -> new RuntimeException("No summary found for this month"));

        if (summary.getTotalIncome() == null) summary.setTotalIncome(BigDecimal.ZERO);
        if (summary.getBasicosSpent() == null) summary.setBasicosSpent(BigDecimal.ZERO);
        if (summary.getAhorroSpent() == null) summary.setAhorroSpent(BigDecimal.ZERO);
        if (summary.getLujosSpent() == null) summary.setLujosSpent(BigDecimal.ZERO);

        switch (category.toLowerCase()) {
            case "basicos" -> {
                summary.setBasicosSpent(summary.getBasicosSpent().add(amount));
            }
            case "ahorro" -> {
                summary.setAhorroSpent(summary.getAhorroSpent().add(amount));
            }
            case "lujos" -> {
                summary.setLujosSpent(summary.getLujosSpent().add(amount));
            }
            default -> throw new RuntimeException("Invalid category: " + category);
        }

        monthlySummaryRepository.save(summary);

        Transaction transaction = new Transaction(
                user,
                "expense",
                category,
                amount,
                description,
                now
        );

        return transactionRepository.save(transaction);
    }

    public void addDirectSaving(User user, BigDecimal amount) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        MonthlySummary summary = monthlySummaryRepository
                .findByUserIdAndYearAndMonth(user.getId(), year, month)
                .orElse(new MonthlySummary(user, year, month));

        if (summary.getAhorroDirecto() == null) {
            summary.setAhorroDirecto(BigDecimal.ZERO);
        }

        // Sumamos el ahorro directo
        summary.setAhorroDirecto(summary.getAhorroDirecto().add(amount));

        monthlySummaryRepository.save(summary);

        // Guardar transacción (opcional, para historial)
        Transaction tx = new Transaction(
                user,
                "saving",
                "direct",
                amount,
                "Ahorro directo",
                now
        );
        transactionRepository.save(tx);
    }

    @Transactional
    public void closeMonth(User user, int year, int month) {
        Optional<MonthlySummary> optionalSummary =
                monthlySummaryRepository.findByUserIdAndYearAndMonth(user.getId(), year, month);

        if (optionalSummary.isPresent()) {
            MonthlySummary summary = optionalSummary.get();

            if (summary.isClosed()) {
                System.out.println("⚠️ Resumen ya cerrado para " + user.getEmail() + " mes " + month);
                return;
            }

            // 🔹 Calcular sobrante
            BigDecimal basicosTotal = summary.getTotalIncome().multiply(BigDecimal.valueOf(0.5));
            BigDecimal lujosTotal = summary.getTotalIncome().multiply(BigDecimal.valueOf(0.3));

            BigDecimal basicosRestante = basicosTotal.subtract(summary.getBasicosSpent() != null ? summary.getBasicosSpent() : BigDecimal.ZERO);
            BigDecimal lujosRestante = lujosTotal.subtract(summary.getLujosSpent() != null ? summary.getLujosSpent() : BigDecimal.ZERO);

            BigDecimal transfer = basicosRestante.add(lujosRestante);

            if (transfer.compareTo(BigDecimal.ZERO) > 0) {
                summary.setAutomaticTransfer(summary.getAutomaticTransfer().add(transfer));
            }

            summary.setTotalIncome(BigDecimal.ZERO);
            summary.setBasicosSpent(BigDecimal.ZERO);
            summary.setAhorroSpent(BigDecimal.ZERO);
            summary.setLujosSpent(BigDecimal.ZERO);

            summary.setClosed(true);

            monthlySummaryRepository.save(summary);

            System.out.println("✅ Resumen cerrado para " + user.getEmail() + " mes " + month);
        } else {
            System.out.println("❌ No se encontró resumen para " + user.getEmail() + " mes " + month);
        }
    }

}

