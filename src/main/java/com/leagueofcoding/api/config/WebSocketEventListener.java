package com.leagueofcoding.api.config;

import com.leagueofcoding.api.dto.websocket.ChatMessage;
import com.leagueofcoding.api.dto.websocket.UserStatusMessage;
import com.leagueofcoding.api.service.OnlineUsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

/**
 * WebSocketEventListener - Handle WebSocket lifecycle events.
 *
 * @author dao-nguyenminh
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final OnlineUsersService onlineUsersService;

    /**
     * Handle WebSocket connection established.
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("WebSocket connection established");
    }

    /**
     * Handle WebSocket disconnection.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");

        if (username != null) {
            log.info("User disconnected: {}", username);

            // Remove from online users
            onlineUsersService.removeUser(username);

            // Broadcast leave message
            ChatMessage leaveMessage = ChatMessage.leave(username);
            messagingTemplate.convertAndSend("/topic/public", leaveMessage);

            // Broadcast user status
            UserStatusMessage status = UserStatusMessage.offline(
                    username,
                    onlineUsersService.getOnlineCount()
            );
            messagingTemplate.convertAndSend("/topic/user.status", status);
        }
    }
}