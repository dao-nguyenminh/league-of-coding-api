package com.leagueofcoding.api.dto.websocket;

import java.time.LocalDateTime;

/**
 * ChatMessage - DTO cho chat messages.
 *
 * @author dao-nguyenminh
 */
public record ChatMessage(
        MessageType type,
        String content,
        String sender,
        LocalDateTime timestamp
) {
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public static ChatMessage chat(String content, String sender) {
        return new ChatMessage(MessageType.CHAT, content, sender, LocalDateTime.now());
    }

    public static ChatMessage join(String sender) {
        return new ChatMessage(MessageType.JOIN, sender + " joined", sender, LocalDateTime.now());
    }

    public static ChatMessage leave(String sender) {
        return new ChatMessage(MessageType.LEAVE, sender + " left", sender, LocalDateTime.now());
    }
}