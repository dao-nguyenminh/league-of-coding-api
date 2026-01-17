package com.leagueofcoding.api.dto.websocket;

import java.time.LocalDateTime;

/**
 * UserStatusMessage - DTO cho user online status.
 *
 * @author dao-nguyenminh
 */
public record UserStatusMessage(
        String username,
        String status,  // ONLINE, OFFLINE, AWAY
        Long onlineCount,
        LocalDateTime timestamp
) {

    public static UserStatusMessage online(String username, Long onlineCount) {
        return new UserStatusMessage(
                username,
                "ONLINE",
                onlineCount,
                LocalDateTime.now()
        );
    }

    public static UserStatusMessage offline(String username, Long onlineCount) {
        return new UserStatusMessage(
                username,
                "OFFLINE",
                onlineCount,
                LocalDateTime.now()
        );
    }
}