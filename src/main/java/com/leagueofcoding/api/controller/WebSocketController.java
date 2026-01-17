package com.leagueofcoding.api.controller;

import com.leagueofcoding.api.dto.websocket.ChatMessage;
import com.leagueofcoding.api.dto.websocket.NotificationMessage;
import com.leagueofcoding.api.dto.websocket.UserStatusMessage;
import com.leagueofcoding.api.service.OnlineUsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Objects;

/**
 * WebSocketController - Handle WebSocket messages.
 *
 * @author dao-nguyenminh
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final OnlineUsersService onlineUsersService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle chat messages.
     * Client sends to: /app/chat.send
     * Server broadcasts to: /topic/public
     */
    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage message) {
        log.info("Chat message from {}: {}", message.sender(), message.content());
        return message;
    }

    /**
     * Handle user connect.
     * Client sends to: /app/user.connect
     */
    @MessageMapping("/user.connect")
    public void userConnect(
            @Payload UserStatusMessage message,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        String username = message.username();

        // Store username in session
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", username);

        // Add to online users
        onlineUsersService.addUser(username);
        log.info("User connected: {} (Total online: {})",
                username, onlineUsersService.getOnlineCount());

        // Broadcast status
        UserStatusMessage status = UserStatusMessage.online(
                username,
                onlineUsersService.getOnlineCount()
        );
        messagingTemplate.convertAndSend("/topic/user-status", status);
    }

    /**
     * Handle user disconnect.
     * Client sends to: /app/user.disconnect
     */
    @MessageMapping("/user.disconnect")
    public void userDisconnect(@Payload UserStatusMessage message) {
        String username = message.username();

        onlineUsersService.removeUser(username);
        log.info("User disconnected: {} (Total online: {})",
                username, onlineUsersService.getOnlineCount());

        // Broadcast status
        UserStatusMessage status = UserStatusMessage.offline(
                username,
                onlineUsersService.getOnlineCount()
        );
        messagingTemplate.convertAndSend("/topic/user-status", status);
    }

    /**
     * Handle notifications.
     * Client sends to: /app/notification.send
     */
    @MessageMapping("/notification.send")
    @SendTo("/topic/notifications")
    public NotificationMessage sendNotification(@Payload NotificationMessage notification) {
        log.info("Notification: {} - {}", notification.type(), notification.message());
        return notification;
    }
}