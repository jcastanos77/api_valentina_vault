package com.mv.api_valentinasvault.service;

import com.mv.api_valentinasvault.model.Notification;
import com.mv.api_valentinasvault.model.User;
import com.mv.api_valentinasvault.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(User user, String title, String message, String type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        return notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndReadFalse(user);
    }

    public void markAsRead(Notification notification) {
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public Optional<Notification> getNotificationById(UUID id) {
        return notificationRepository.findById(id);
    }

}
