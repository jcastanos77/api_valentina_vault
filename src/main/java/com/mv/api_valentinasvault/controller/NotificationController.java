package com.mv.api_valentinasvault.controller;

import com.mv.api_valentinasvault.model.Notification;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.repository.UserRepository;
import com.mv.api_valentinasvault.service.NotificationService;
import com.mv.api_valentinasvault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService, UserService userService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Map<String, Object>>> getUnreadNotifications(
            @RequestHeader("Authorization") String authHeader) {

        String email = userService.getEmailFromToken(authHeader);
        User user = userService.findByEmail(email);

        List<Notification> notifications = notificationService.getUnreadNotifications(user);

        List<Map<String, Object>> result = notifications.stream().map(n -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", n.getId());
            map.put("title", n.getTitle());
            map.put("message", n.getMessage());
            map.put("type", n.getType());
            map.put("createdAt", n.getCreatedAt());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/mark-read/{id}")
    public ResponseEntity<String> markAsRead(@PathVariable UUID id) {
        Optional<Notification> notificationOpt = notificationService.getNotificationById(id);

        if (notificationOpt.isPresent()) {
            notificationService.markAsRead(notificationOpt.get());
            return ResponseEntity.ok("Notification marked as read");
        } else {
            return ResponseEntity.status(404).body("Notification not found");
        }
    }

}

