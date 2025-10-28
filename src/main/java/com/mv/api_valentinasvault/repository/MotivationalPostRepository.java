package com.mv.api_valentinasvault.repository;

import com.mv.api_valentinasvault.model.MotivationalPost;
import com.mv.api_valentinasvault.model.MotivationalComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MotivationalPostRepository extends JpaRepository<MotivationalPost, UUID> {
    List<MotivationalPost> findAllByOrderByCreatedAtDesc();
}