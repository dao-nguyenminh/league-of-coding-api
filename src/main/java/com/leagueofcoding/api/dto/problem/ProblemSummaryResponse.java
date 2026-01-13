package com.leagueofcoding.api.dto.problem;


import com.leagueofcoding.api.entity.Problem;
import com.leagueofcoding.api.enums.Difficulty;

/**
 * ProblemSummaryResponse - DTO cho problem listing (without full description).
 *
 * @author dao-nguyenminh
 */
public record ProblemSummaryResponse(
        Long id,
        String title,
        String slug,
        Difficulty difficulty,
        CategoryResponse category
) {
    public static ProblemSummaryResponse from(Problem problem) {
        return new ProblemSummaryResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getSlug(),
                problem.getDifficulty(),
                CategoryResponse.from(problem.getCategory())
        );
    }
}