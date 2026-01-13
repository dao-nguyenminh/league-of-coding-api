package com.leagueofcoding.api.dto.problem;

import com.leagueofcoding.api.entity.Problem;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * ProblemSummaryResponse - DTO cho problem listing (không có chi tiết).
 *
 * @author dao-nguyenminh
 */
@Builder
public record ProblemSummaryResponse(
        Long id,
        String title,
        String slug,
        String difficulty,
        String categoryName,
        String categorySlug,
        LocalDateTime createdAt
) {

    public static ProblemSummaryResponse from(Problem problem) {
        return ProblemSummaryResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .slug(problem.getSlug())
                .difficulty(problem.getDifficulty().name())
                .categoryName(problem.getCategory().getName())
                .categorySlug(problem.getCategory().getSlug())
                .createdAt(problem.getCreatedAt())
                .build();
    }
}