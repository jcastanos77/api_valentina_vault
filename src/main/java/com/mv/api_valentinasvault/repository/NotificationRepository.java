package com.mv.api_valentinasvault.repository;

import com.mv.api_valentinasvault.model.Notification;
import com.mv.api_valentinasvault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserAndReadFalse(User user);
}
