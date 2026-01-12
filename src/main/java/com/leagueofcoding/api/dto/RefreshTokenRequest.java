package com.leagueofcoding.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * RefreshTokenRequest - DTO cho refresh token request.
 *
 * @author dao-nguyenminh
 */
public record RefreshTokenRequest(

        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}