package com.leagueofcoding.api.dto.problem;

import com.leagueofcoding.api.entity.Problem;
import com.leagueofcoding.api.enums.Difficulty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ProblemResponse - DTO cho problem details.
 *
 * @author dao-nguyenminh
 */
public record ProblemResponse(
        Long id,
        String title,
        String slug,
        String description,
        Difficulty difficulty,
        Integer timeLimitMs,
        Integer memoryLimitMb,
        CategoryResponse category,
        List<TestCaseResponse> sampleTestCases,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProblemResponse from(Problem problem, List<TestCaseResponse> sampleTestCases) {
        return new ProblemResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getSlug(),
                problem.getDescription(),
                problem.getDifficulty(),
                problem.getTimeLimitMs(),
                problem.getMemoryLimitMb(),
                CategoryResponse.from(problem.getCategory()),
                sampleTestCases,
                problem.getIsActive(),
                problem.getCreatedAt(),
                problem.getUpdatedAt()
        );
    }
}