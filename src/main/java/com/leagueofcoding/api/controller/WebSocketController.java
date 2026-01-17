package com.leagueofcoding.api.controller;

import com.leagueofcoding.api.dto.websocket.ChatMessage;
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
 * WebSocketController - WebSocket message handlers.
 *
 * @author dao-nguyenminh
 * @since 2025-01-16
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final OnlineUsersService onlineUsersService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle chat messages.
     * <p>
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
     * Handle user join.
     * <p>
     * Client sends to: /app/chat.join
     * Server broadcasts to: /topic/public
     */
    @MessageMapping("/chat.join")
    @SendTo("/topic/public")
    public ChatMessage joinUser(
            @Payload ChatMessage message,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        String username = message.sender();

        // Add username to WebSocket session
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", username);

        // Track online user
        onlineUsersService.addUser(username);

        // Broadcast user status
        UserStatusMessage status = UserStatusMessage.online(
                username,
                onlineUsersService.getOnlineCount()
        );
        messagingTemplate.convertAndSend("/topic/user.status", status);

        log.info("User joined: {}", username);
        return ChatMessage.join(username);
    }

    /**
     * Send notification to specific user.
     * <p>
     * Example: Send match found notification
     */
    public void sendNotificationToUser(String username, Object notification) {
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notifications",
                notification
        );
        log.info("Sent notification to user: {}", username);
    }

    /**
     * Broadcast notification to all users.
     */
    public void broadcastNotification(Object notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        log.info("Broadcast notification to all users");
    }
}