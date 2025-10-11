package com.mv.api_valentinasvault.service;

import com.mv.api_valentinasvault.model.SavingsGoal;
import com.mv.api_valentinasvault.model.Transaction;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.repository.SavingsGoalRepository;
import com.mv.api_valentinasvault.repository.TransactionRepository;
import com.mv.api_valentinasvault.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class SavingsService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final TransactionRepository transactionRepository;

    private final UserRepository userRepository;

    public SavingsService(SavingsGoalRepository savingsGoalRepository, TransactionRepository transactionRepository, UserRepository userRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<Map<String, Object>> getUserSavingsProgress(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<SavingsGoal> goals = savingsGoalRepository.findByUserIdAndIsActiveTrue(user.getId());
        List<Transaction> transactions = transactionRepository.findByUserId(user.getId());


        // Calcular total ahorrado (solo transacciones tipo "income" con categoría "ahorro")
        double totalSaved = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("income") && t.getCategory().equalsIgnoreCase("ahorro"))
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();

        List<Map<String, Object>> progressList = new ArrayList<>();

        for (SavingsGoal goal : goals) {
            Map<String, Object> progress = new HashMap<>();
            progress.put("goalName", goal.getName());
            progress.put("targetAmount", goal.getTargetAmount());
            progress.put("currentAmount", goal.getCurrentAmount().add(java.math.BigDecimal.valueOf(totalSaved)));
            progress.put("remaining", goal.getTargetAmount().subtract(goal.getCurrentAmount().add(java.math.BigDecimal.valueOf(totalSaved))));
            progress.put("isCompleted", goal.getCurrentAmount().add(java.math.BigDecimal.valueOf(totalSaved))
                    .compareTo(goal.getTargetAmount()) >= 0);

            progressList.add(progress);
        }

        return progressList;
    }

    public Map<String, Object> addSavingsGoal(String email, String name, Double targetAmount) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SavingsGoal goal = new SavingsGoal();
        goal.setUser(user);
        goal.setName(name);
        goal.setTargetAmount(BigDecimal.valueOf(targetAmount));
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setActive(true);

        savingsGoalRepository.save(goal);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Savings goal created successfully");
        response.put("goalId", goal.getId());
        response.put("name", goal.getName());
        response.put("targetAmount", goal.getTargetAmount());

        return response;
    }

    public List<Map<String, Object>> getUserSavingsProgressByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return getUserSavingsProgress(email);
    }

}
