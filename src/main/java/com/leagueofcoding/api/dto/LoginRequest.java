package com.leagueofcoding.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest - DTO cho user login.
 *
 * @author dao-nguyenminh
 */
public record LoginRequest(

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {
}