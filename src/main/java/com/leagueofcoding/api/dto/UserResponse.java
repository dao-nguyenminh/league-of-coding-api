package com.leagueofcoding.api.dto;

import com.leagueofcoding.api.entity.User;

import java.time.LocalDateTime;

/**
 * UserResponse - DTO chứa thông tin user (không có password).
 *
 * @author dao-nguyenminh
 */
public record UserResponse(
        Long id,
        String username,
        String email,
        Integer rating,
        Integer totalMatches,
        Integer wins,
        Integer losses,
        LocalDateTime createdAt
) {
    /**
     * Factory method tạo UserResponse từ User entity.
     */
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRating(),
                user.getTotalMatches(),
                user.getWins(),
                user.getLosses(),
                user.getCreatedAt()
        );
    }
}