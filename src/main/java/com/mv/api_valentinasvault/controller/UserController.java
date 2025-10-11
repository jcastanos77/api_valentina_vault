package com.mv.api_valentinasvault.controller;

import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.model.UserRule;
import com.mv.api_valentinasvault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rules")
    public ResponseEntity<Map<String, Integer>> getUserRules(@RequestHeader("Authorization") String authHeader) {
        String email = userService.getEmailFromToken(authHeader);
        UserRule userRules = userService.getRulesByEmail(email);

        Map<String, Integer> response = Map.of(
                "basicosPercent", userRules.getBasicosPercent(),
                "ahorroPercent", userRules.getAhorroPercent(),
                "lujosPercent", userRules.getLujosPercent()
        );
        return ResponseEntity.ok(response);
    }
}
