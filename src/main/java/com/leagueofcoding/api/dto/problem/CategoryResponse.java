package com.leagueofcoding.api.dto.problem;

import com.leagueofcoding.api.entity.Category;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * CategoryResponse - DTO cho category responses.
 *
 * @author dao-nguyenminh
 */
@Builder
public record CategoryResponse(
        Long id,
        String name,
        String slug,
        String description,
        Long problemCount,
        LocalDateTime createdAt
) {

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .problemCount(category.getProblems() != null ?
                        (long) category.getProblems().size() : 0L)
                .createdAt(category.getCreatedAt())
                .build();
    }
}