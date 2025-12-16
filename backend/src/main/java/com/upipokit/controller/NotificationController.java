package com.upipokit.controller;

import com.upipokit.entity.Notification;
import com.upipokit.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    NotificationService notificationService;
    
    /**
     * Get all notifications for a user
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByUser(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Get unread notifications for a user
     */
    @GetMapping("/{userId}/unread")
    public ResponseEntity<?> getUnreadNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotificationsByUser(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Mark a notification as read
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        try {
            Notification notification = notificationService.markAsRead(notificationId);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Mark all notifications as read for a user
     */
    @PutMapping("/{userId}/read-all")
    public ResponseEntity<?> markAllAsRead(@PathVariable Long userId) {
        try {
            int count = notificationService.markAllAsRead(userId);
            return ResponseEntity.ok("Marked " + count + " notifications as read");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}