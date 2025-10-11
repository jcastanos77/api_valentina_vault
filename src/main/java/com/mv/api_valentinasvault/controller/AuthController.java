package com.mv.api_valentinasvault.controller;

import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.repository.UserRepository;
import com.mv.api_valentinasvault.service.AuthService;
import com.mv.api_valentinasvault.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private AuthService authService;

    // 👉 Registro
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo ya está en uso");
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        userRepository.save(user);

        int basicos = user.getBasicosPercent() != null ? user.getBasicosPercent() : 50;
        int ahorro  = user.getAhorroPercent()  != null ? user.getAhorroPercent()  : 30;
        int lujos   = user.getLujosPercent()   != null ? user.getLujosPercent()   : 20;

        authService.addUserRule(user,basicos,ahorro,lujos);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Usuario registrado correctamente");
        return ResponseEntity.ok(response);
    }

    // 👉 Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.get("email"), request.get("password"))
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            Date timeExpired = jwtUtil.extractExpiration(token);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("timeExpired", timeExpired);
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }
}
