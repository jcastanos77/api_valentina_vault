package com.mv.api_valentinasvault.service;

import com.mv.api_valentinasvault.model.MotivationalPost;
import com.mv.api_valentinasvault.model.MotivationalComment;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.repository.MotivationalPostRepository;
import com.mv.api_valentinasvault.repository.MotivationalCommentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class MotivationService {

    private final MotivationalPostRepository postRepository;
    private final MotivationalCommentRepository commentRepository;

    public MotivationService(MotivationalPostRepository postRepository, MotivationalCommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public MotivationalPost createPost(User user, String content) {
        MotivationalPost post = new MotivationalPost();
        post.setUser(user);
        post.setContent(content);
        return postRepository.save(post);
    }

    public List<MotivationalPost> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public MotivationalComment addComment(User user, UUID postId, String content) {
        MotivationalPost post = postRepository.findById(postId).orElseThrow();
        MotivationalComment comment = new MotivationalComment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public List<MotivationalComment> getComments(UUID postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }
}
