package com.mv.api_valentinasvault.service;

import com.mv.api_valentinasvault.model.UserRule;
import com.mv.api_valentinasvault.repository.UserRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.repository.UserRepository;
import com.mv.api_valentinasvault.utils.JWTUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final UserRuleRepository userRuleRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    private final JWTUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, UserRuleRepository userRuleRepository,
                       JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userRuleRepository = userRuleRepository;
        this.jwtUtil = jwtUtil;
    }

    public String register(User user) {
        user.setEmail(user.getEmail());
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        userRepository.save(user);
        return "User registered successfully";
    }

    public String login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);


        return jwtUtil.generateToken(userDetails);
    }

    public void addUserRule(User user, int basico, int ahorro, int lujo){

        validateRule(basico, ahorro, lujo);

        UserRule rule = new UserRule(user, basico, ahorro, lujo);
        userRuleRepository.save(rule);
    }

    private void validateRule(int basicos, int ahorro, int lujos) {
        if (basicos < 40 || basicos > 60) {
            throw new IllegalArgumentException("Básicos debe estar entre 40% y 60%");
        }
        if (ahorro < 10) {
            throw new IllegalArgumentException("Ahorro no puede ser menor al 10%");
        }
        if (lujos > 30) {
            throw new IllegalArgumentException("Lujos no puede ser mayor al 30%");
        }
        if (basicos + ahorro + lujos != 100) {
            throw new IllegalArgumentException("La regla debe sumar 100%");
        }
    }

}
