package com.leagueofcoding.api.repository;

import com.leagueofcoding.api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CategoryRepository - Data access cho categories.
 *
 * @author dao-nguyenminh
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by slug.
     */
    Optional<Category> findBySlug(String slug);

    /**
     * Check if category exists by name.
     */
    boolean existsByName(String name);

    /**
     * Check if category exists by slug.
     */
    boolean existsBySlug(String slug);

    /**
     * Count problems in category.
     */
    @Query("SELECT COUNT(p) FROM Problem p WHERE p.category.id = :categoryId AND p.isActive = true")
    long countActiveProblemsByCategoryId(@Param("categoryId") Long categoryId);
}