package com.leagueofcoding.api.dto.websocket;

/**
 * UserStatusMessage - DTO cho user online status.
 *
 * @author dao-nguyenminh
 */
public record UserStatusMessage(
        String username,
        UserStatus status,
        Long onlineCount
) {
    public enum UserStatus {
        ONLINE,
        OFFLINE,
        IN_MATCH,
        IN_QUEUE
    }

    public static UserStatusMessage online(String username, Long count) {
        return new UserStatusMessage(username, UserStatus.ONLINE, count);
    }

    public static UserStatusMessage offline(String username, Long count) {
        return new UserStatusMessage(username, UserStatus.OFFLINE, count);
    }
}