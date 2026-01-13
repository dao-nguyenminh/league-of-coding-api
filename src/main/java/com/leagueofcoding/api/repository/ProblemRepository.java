package com.leagueofcoding.api.repository;

import com.leagueofcoding.api.entity.Problem;
import com.leagueofcoding.api.enums.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
     * Search problems by query with filters.
     */
    @Query("SELECT p FROM Problem p WHERE " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND p.isActive = true")
    Page<Problem> searchByQueryAndActive(@Param("query") String query, Pageable pageable);

    @Query("SELECT p FROM Problem p WHERE " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND p.difficulty = :difficulty AND p.isActive = true")
    Page<Problem> searchByQueryAndDifficultyAndActive(
            @Param("query") String query,
            @Param("difficulty") Difficulty difficulty,
            Pageable pageable
    );

    @Query("SELECT p FROM Problem p WHERE " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND p.category.id = :categoryId AND p.isActive = true")
    Page<Problem> searchByQueryAndCategoryAndActive(
            @Param("query") String query,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    @Query("SELECT p FROM Problem p WHERE " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND p.category.id = :categoryId AND p.difficulty = :difficulty AND p.isActive = true")
    Page<Problem> searchByQueryAndCategoryAndDifficultyAndActive(
            @Param("query") String query,
            @Param("categoryId") Long categoryId,
            @Param("difficulty") Difficulty difficulty,
            Pageable pageable
    );

    /**
     * Get random problems for practice.
     */
    @Query(value = "SELECT * FROM problems WHERE is_active = true ORDER BY RANDOM() LIMIT :count",
            nativeQuery = true)
    List<Problem> findRandomByActive(@Param("count") int count);

    @Query(value = "SELECT * FROM problems WHERE is_active = true AND difficulty = :difficulty " +
            "ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    List<Problem> findRandomByDifficultyAndActive(
            @Param("difficulty") String difficulty,
            @Param("count") int count
    );

    @Query(value = "SELECT * FROM problems WHERE is_active = true AND category_id = :categoryId " +
            "ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    List<Problem> findRandomByCategoryAndActive(
            @Param("categoryId") Long categoryId,
            @Param("count") int count
    );

    @Query(value = "SELECT * FROM problems WHERE is_active = true AND category_id = :categoryId " +
            "AND difficulty = :difficulty ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    List<Problem> findRandomByCategoryAndDifficultyAndActive(
            @Param("categoryId") Long categoryId,
            @Param("difficulty") String difficulty,
            @Param("count") int count
    );

    /**
     * Check if slug exists.
     */
    boolean existsBySlug(String slug);
}