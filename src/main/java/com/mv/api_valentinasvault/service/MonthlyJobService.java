package com.mv.api_valentinasvault.service;

import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MonthlyJobService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    // Se ejecuta a las 00:05 del día 1 de cada mes
    @Scheduled(cron = "0 5 0 1 * *")
    public void closePreviousMonth() {
        LocalDate now = LocalDate.now();
        LocalDate lastMonthDate = now.minusMonths(1);

        int lastMonth = lastMonthDate.getMonthValue();
        int year = lastMonthDate.getYear();

        List<User> users = userRepository.findAll();

        for (User user : users) {
            transactionService.closeMonth(user, year, lastMonth);
        }

        System.out.println("✅ Cierre de mes ejecutado para " + users.size() + " usuarios");
    }
}
