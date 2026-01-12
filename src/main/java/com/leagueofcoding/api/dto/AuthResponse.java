package com.leagueofcoding.api.dto;

/**
 * AuthResponse - Response chứa access token, refresh token và user info.
 *
 * @author dao-nguyenminh
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        UserResponse user
) {
    public AuthResponse(String accessToken, String refreshToken, UserResponse user) {
        this(accessToken, refreshToken, "Bearer", user);
    }
}