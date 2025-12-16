package com.upipokit.controller;

import com.upipokit.entity.Notification;
import com.upipokit.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWebSocketController {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Send notification to a specific user
     * @param notification the notification to send
     */
    public void sendNotificationToUser(Notification notification) {
        // Send to the user's notification channel
        messagingTemplate.convertAndSend("/topic/notifications/" + notification.getUser().getId(), notification);
    }
    
    /**
     * Handle incoming messages (if needed)
     * @param message the incoming message
     */
    @MessageMapping("/notification")
    @SendTo("/topic/notifications")
    public String handleNotificationMessage(@Payload String message) {
        return message;
    }
}