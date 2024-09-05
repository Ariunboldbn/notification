package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Notification;
import com.example.demo.entity.NotificationReadStatus;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationReadStatusRepository;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationReadStatusRepository notificationReadStatusRepository;

    @Autowired
    private UserRepository userRepository;

    public Notification createNotification(String type, String title, String message, String data, List<Long> userIds) {
        Notification notification = new Notification();
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setData(data);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());

        List<User> users = userRepository.findAllById(userIds);
        notification.setUsers(users);

        Notification savedNotification = notificationRepository.save(notification);

        for (User user : users) {
            NotificationReadStatus readStatus = new NotificationReadStatus();
            readStatus.setNotification(savedNotification);
            readStatus.setUser(user);
            readStatus.setIsRead(false);
            notificationReadStatusRepository.save(readStatus);
        }

        return savedNotification;
    }

    public void markAsRead(Long notificationId, Long userId) {
        List<NotificationReadStatus> readStatuses = notificationReadStatusRepository.findByUserId(userId);
        for (NotificationReadStatus status : readStatuses) {
            if (status.getNotification().getId().equals(notificationId)) {
                status.setIsRead(true);
                notificationReadStatusRepository.save(status);
                break;
            }
        }
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        List<NotificationReadStatus> readStatuses = notificationReadStatusRepository.findByUserId(userId);
        return readStatuses.stream()
                .map(NotificationReadStatus::getNotification)
                .collect(Collectors.toList());
    }
}
