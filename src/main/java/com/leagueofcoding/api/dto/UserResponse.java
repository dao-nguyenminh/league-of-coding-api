package com.leagueofcoding.api.dto;

import com.leagueofcoding.api.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * UserResponse - DTO cho user responses.
 *
 * @author dao-nguyenminh
 */
@Builder
public record UserResponse(
        Long id,
        String username,
        String email,
        String role,
        Integer rating,
        Integer totalMatches,
        Integer wins,
        Integer losses,
        LocalDateTime createdAt
) {

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .rating(user.getRating())
                .totalMatches(user.getTotalMatches())
                .wins(user.getWins())
                .losses(user.getLosses())
                .createdAt(user.getCreatedAt())
                .build();
    }
}