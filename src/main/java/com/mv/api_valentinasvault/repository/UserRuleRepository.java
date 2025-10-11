package com.mv.api_valentinasvault.repository;

import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.model.UserRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRuleRepository extends JpaRepository<UserRule, UUID> {
    Optional<UserRule> findByUser(User user);

    Optional<UserRule> findByUserId(UUID userId);
}
