package com.upipokit.service;

import com.upipokit.entity.Notification;
import com.upipokit.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    
    @Autowired
    NotificationRepository notificationRepository;
    
    /**
     * Get all notifications for a user
     * @param userId the ID of the user
     * @return list of notifications
     */
    public List<Notification> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Get unread notifications for a user
     * @param userId the ID of the user
     * @return list of unread notifications
     */
    public List<Notification> getUnreadNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Mark a notification as read
     * @param notificationId the ID of the notification
     * @return the updated notification
     */
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }
    
    /**
     * Mark all notifications as read for a user
     * @param userId the ID of the user
     * @return number of notifications marked as read
     */
    public int markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = getUnreadNotificationsByUser(userId);
        int count = 0;
        
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
            count++;
        }
        
        return count;
    }
}