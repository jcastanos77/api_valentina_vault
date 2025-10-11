package com.mv.api_valentinasvault.service;

import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.model.UserRule;
import com.mv.api_valentinasvault.repository.UserRepository;
import com.mv.api_valentinasvault.repository.UserRuleRepository;
import com.mv.api_valentinasvault.utils.JWTUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final UserRuleRepository userRuleRepository;
    public UserService(UserRepository userRepository, JWTUtil jwtUtil, UserRuleRepository userRuleRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userRuleRepository = userRuleRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Obtener email desde token
    public String getEmailFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUsername(token);
    }

    public UserRule getRulesByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return userRuleRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Reglas no definidas para este usuario"));

    }
}
