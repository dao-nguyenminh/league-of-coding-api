package com.leagueofcoding.api.dto;

/**
 * AuthResponse - Response chứa JWT token và user info.
 *
 * @author dao-nguyenminh
 */
public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user
) {
    // Compact constructor với default tokenType
    public AuthResponse(String token, UserResponse user) {
        this(token, "Bearer", user);
    }
}