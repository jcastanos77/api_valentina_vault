package com.mv.api_valentinasvault.repository;

import com.mv.api_valentinasvault.model.MotivationalComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
public interface MotivationalCommentRepository extends JpaRepository<MotivationalComment, UUID> {
    List<MotivationalComment> findByPostIdOrderByCreatedAtAsc(UUID postId);
}