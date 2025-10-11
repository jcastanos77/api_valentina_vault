package com.mv.api_valentinasvault.service;

import com.mv.api_valentinasvault.model.MonthlySummary;
import com.mv.api_valentinasvault.model.Transaction;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.repository.MonthlySummaryRepository;
import com.mv.api_valentinasvault.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class MonthlySummaryService {

    private final MonthlySummaryRepository monthlySummaryRepository;
    private final TransactionRepository transactionRepository;
    public MonthlySummaryService(MonthlySummaryRepository monthlySummaryRepository, TransactionRepository transactionRepository) {
        this.monthlySummaryRepository = monthlySummaryRepository;
        this.transactionRepository = transactionRepository;
    }

    // Obtener resumen mensual
    public MonthlySummary getMonthlySummary(User user, int year, int month) {
        return monthlySummaryRepository.findByUserAndYearAndMonth(user, year, month)
                .orElseGet(() -> {
                    MonthlySummary summary = new MonthlySummary();
                    summary.setUser(user);
                    summary.setYear(year);
                    summary.setMonth(month);
                    summary.setTotalIncome(java.math.BigDecimal.ZERO);
                    summary.setBasicosSpent(java.math.BigDecimal.ZERO);
                    summary.setAhorroSpent(java.math.BigDecimal.ZERO);
                    summary.setLujosSpent(java.math.BigDecimal.ZERO);
                    return summary;
                });
    }

}

