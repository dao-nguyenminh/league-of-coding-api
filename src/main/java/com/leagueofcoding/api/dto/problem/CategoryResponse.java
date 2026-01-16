package com.leagueofcoding.api.dto.problem;

import com.leagueofcoding.api.entity.Category;

/**
 * CategoryResponse - DTO cho category info.
 *
 * @author dao-nguyenminh
 */
public record CategoryResponse(
        Long id,
        String name,
        String slug,
        String description
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription()
        );
    }
}