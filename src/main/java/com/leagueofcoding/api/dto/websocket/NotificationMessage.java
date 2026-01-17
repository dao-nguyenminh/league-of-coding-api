package com.leagueofcoding.api.dto.websocket;

import java.time.LocalDateTime;

/**
 * NotificationMessage - DTO cho system notifications.
 *
 * @author dao-nguyenminh
 */
public record NotificationMessage(
        NotificationType type,
        String title,
        String message,
        Object data,
        LocalDateTime timestamp
) {
    public enum NotificationType {
        INFO,
        SUCCESS,
        WARNING,
        ERROR,
        MATCH_FOUND,
        BATTLE_STARTED,
        BATTLE_ENDED
    }

    public static NotificationMessage info(String title, String message) {
        return new NotificationMessage(
                NotificationType.INFO,
                title,
                message,
                null,
                LocalDateTime.now()
        );
    }

    public static NotificationMessage matchFound(String message, Object matchData) {
        return new NotificationMessage(
                NotificationType.MATCH_FOUND,
                "Match Found!",
                message,
                matchData,
                LocalDateTime.now()
        );
    }
}