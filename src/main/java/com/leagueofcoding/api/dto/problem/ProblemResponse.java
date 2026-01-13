package com.leagueofcoding.api.dto.problem;

import com.leagueofcoding.api.entity.Problem;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ProblemResponse - DTO cho detailed problem responses.
 *
 * @author dao-nguyenminh
 */
@Builder
public record ProblemResponse(
        Long id,
        String title,
        String slug,
        String description,
        String difficulty,
        Integer timeLimitMs,
        Integer memoryLimitMb,
        String categoryName,
        String categorySlug,
        String createdBy,
        LocalDateTime createdAt,
        List<TestCaseResponse> sampleTestCases
) {

    public static ProblemResponse from(Problem problem, List<TestCaseResponse> sampleTestCases) {
        return ProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .slug(problem.getSlug())
                .description(problem.getDescription())
                .difficulty(problem.getDifficulty().name())
                .timeLimitMs(problem.getTimeLimitMs())
                .memoryLimitMb(problem.getMemoryLimitMb())
                .categoryName(problem.getCategory().getName())
                .categorySlug(problem.getCategory().getSlug())
                .createdBy(problem.getCreatedBy().getUsername())
                .createdAt(problem.getCreatedAt())
                .sampleTestCases(sampleTestCases)
                .build();
    }
}