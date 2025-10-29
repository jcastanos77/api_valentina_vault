package com.mv.api_valentinasvault.controller;

import com.mv.api_valentinasvault.model.MotivationalPost;
import com.mv.api_valentinasvault.model.MotivationalComment;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.service.MotivationService;
import com.mv.api_valentinasvault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/motivation")
public class MotivationController {

    private final MotivationService motivationService;
    private final UserService userService;

    public MotivationController(MotivationService motivationService, UserService userService) {
        this.motivationService = motivationService;
        this.userService = userService;
    }

    @PostMapping("/post")
    public ResponseEntity<?> createPost(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String, String> body) {
        String email = userService.getEmailFromToken(authHeader);
        User user = userService.findByEmail(email);
        MotivationalPost post = motivationService.createPost(user, body.get("content"));
        return ResponseEntity.ok(Map.of("message", "Post created", "id", post.getId()));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<Map<String, Object>>> getFeed() {
        List<MotivationalPost> posts = motivationService.getAllPosts();

        List<Map<String, Object>> result = posts.stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", item.getId());
                    map.put("userName", item.getUser().getFullName());
                    map.put("content", item.getContent());
                    map.put("createdAt", item.getCreatedAt());
                    map.put("commentCount", item.getComments().size());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/comment/{postId}")
    public ResponseEntity<?> addComment(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID postId,
            @RequestBody Map<String, String> body) {

        String email = userService.getEmailFromToken(authHeader);
        User user = userService.findByEmail(email);
        motivationService.addComment(user, postId, body.get("content"));
        return ResponseEntity.ok(Map.of("message", "Comment added"));
    }

    @GetMapping("/comments/{postId}")
    public ResponseEntity<List<Map<String, Object>>> getComments(@PathVariable UUID postId) {
        List<MotivationalComment> comments = motivationService.getComments(postId);

        List<Map<String, Object>> result = comments.stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", item.getId());
                    map.put("userName", item.getUser().getFullName());
                    map.put("content", item.getContent());
                    map.put("createdAt", item.getCreatedAt());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
