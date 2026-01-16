package com.leagueofcoding.api.repository;

import com.leagueofcoding.api.entity.Problem;
import com.leagueofcoding.api.enums.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ProblemRepository - Data access cho problems.
 *
 * @author dao-nguyenminh
 */
@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    /**
     * Find problem by slug (for public access).
     */
    @Query("SELECT p FROM Problem p WHERE p.slug = :slug AND p.isActive = true")
    Optional<Problem> findBySlugAndActive(@Param("slug") String slug);

    /**
     * Find problem by slug (admin access - including inactive).
     */
    Optional<Problem> findBySlug(String slug);

    /**
     * Find all active problems with pagination.
     */
    Page<Problem> findByIsActiveTrue(Pageable pageable);

    /**
     * Find problems by difficulty.
     */
    @Query("SELECT p FROM Problem p WHERE p.difficulty = :difficulty AND p.isActive = true")
    Page<Problem> findByDifficultyAndActive(@Param("difficulty") Difficulty difficulty, Pageable pageable);

    /**
     * Find problems by category.
     */
    @Query("SELECT p FROM Problem p WHERE p.category.id = :categoryId AND p.isActive = true")
    Page<Problem> findByCategoryAndActive(@Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * Find problems by category and difficulty.
     */
    @Query("SELECT p FROM Problem p WHERE p.category.id = :categoryId " +
            "AND p.difficulty = :difficulty AND p.isActive = true")
    Page<Problem> findByCategoryAndDifficultyAndActive(
            @Param("categoryId") Long categoryId,
            @Param("difficulty") Difficulty difficulty,
            Pageable pageable
    );

    /**
     * Check if slug exists.
     */
    boolean existsBySlug(String slug);
}