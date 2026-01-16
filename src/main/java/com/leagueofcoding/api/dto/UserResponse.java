package com.leagueofcoding.api.dto;

import com.leagueofcoding.api.entity.User;

import java.time.LocalDateTime;

/**
 * UserResponse - DTO cho user information.
 *
 * @author dao-nguyenminh
 */
public record UserResponse(
        Long id,
        String username,
        String email,
        String roles,           // ← ADDED!
        Integer rating,
        Integer totalMatches,
        Integer wins,
        Integer losses,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.getRating(),
                user.getTotalMatches(),
                user.getWins(),
                user.getLosses(),
                user.getCreatedAt()
        );
    }
}