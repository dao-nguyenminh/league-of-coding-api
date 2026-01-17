package com.leagueofcoding.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocketConfig - Spring WebSocket configuration với STOMP.
 *
 * @author dao-nguyenminh
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure message broker.
     * <p>
     * - /app: Prefix cho messages từ client → server
     * - /topic: Broadcast messages (1-to-many)
     * - /queue: Direct messages (1-to-1)
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * Register STOMP endpoints.
     * <p>
     * Endpoint: ws://localhost:8080/ws
     * With SockJS fallback for browsers không support WebSocket
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // CORS - allow all origins (dev only)
                .withSockJS();  // SockJS fallback
    }
}