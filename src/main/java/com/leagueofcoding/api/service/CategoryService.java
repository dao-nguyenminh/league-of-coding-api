package com.leagueofcoding.api.service;

import com.leagueofcoding.api.dto.problem.CategoryResponse;
import com.leagueofcoding.api.entity.Category;
import com.leagueofcoding.api.exception.CategoryNotFoundException;
import com.leagueofcoding.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CategoryService - Business logic cho categories.
 *
 * @author dao-nguyenminh
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Get all categories.
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        log.info("Getting all categories");

        return categoryRepository.findAll()
                .stream()
                .map(this::convertToCategoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get category by slug.
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        log.info("Getting category by slug: {}", slug);

        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category not found with slug: " + slug
                ));

        return convertToCategoryResponse(category);
    }

    /**
     * Convert Category to CategoryResponse with problem count.
     */
    private CategoryResponse convertToCategoryResponse(Category category) {
        long problemCount = categoryRepository.countActiveProblemsByCategoryId(category.getId());

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .problemCount(problemCount)
                .createdAt(category.getCreatedAt())
                .build();
    }
}