package com.leagueofcoding.api.controller;

import com.leagueofcoding.api.dto.problem.*;
import com.leagueofcoding.api.enums.Difficulty;
import com.leagueofcoding.api.service.CategoryService;
import com.leagueofcoding.api.service.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PublicProblemController - Public endpoints cho browsing problems.
 *
 * @author dao-nguyenminh
 */
@Slf4j
@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
@Tag(name = "Public - Problems", description = "Public problem browsing APIs")
public class PublicProblemController {

    private final ProblemService problemService;
    private final CategoryService categoryService;

    @Operation(
            summary = "Get problem by slug",
            description = "Get detailed problem information with sample test cases"
    )
    @ApiResponse(responseCode = "200", description = "Problem found")
    @ApiResponse(responseCode = "404", description = "Problem not found")
    @GetMapping("/{slug}")
    public ResponseEntity<ProblemResponse> getProblemBySlug(
            @Parameter(description = "Problem slug (e.g., two-sum)")
            @PathVariable String slug
    ) {
        log.info("Getting problem by slug: {}", slug);
        ProblemResponse response = problemService.getProblemBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List problems",
            description = "List problems with filtering, sorting and pagination"
    )
    @ApiResponse(responseCode = "200", description = "Problems listed successfully")
    @GetMapping
    public ResponseEntity<Page<ProblemSummaryResponse>> listProblems(
            @Parameter(description = "Filter by difficulty")
            @RequestParam(required = false) Difficulty difficulty,

            @Parameter(description = "Filter by category ID")
            @RequestParam(required = false) Long categoryId,

            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort direction")
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        log.info("Listing problems - difficulty: {}, categoryId: {}, page: {}, size: {}",
                difficulty, categoryId, page, size);

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ProblemSummaryResponse> response = problemService.listProblems(
                difficulty, categoryId, pageable
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all categories",
            description = "Get list of all problem categories"
    )
    @ApiResponse(responseCode = "200", description = "Categories listed successfully")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        log.info("Getting all categories");
        List<CategoryResponse> response = categoryService.getAllCategories();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get category by slug",
            description = "Get category information with problem count"
    )
    @ApiResponse(responseCode = "200", description = "Category found")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @GetMapping("/categories/{slug}")
    public ResponseEntity<CategoryResponse> getCategoryBySlug(
            @Parameter(description = "Category slug (e.g., arrays)")
            @PathVariable String slug
    ) {
        log.info("Getting category by slug: {}", slug);
        CategoryResponse response = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Search problems",
            description = "Search problems by title or description"
    )
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    @GetMapping("/search")
    public ResponseEntity<Page<ProblemSummaryResponse>> searchProblems(
            @Parameter(description = "Search query")
            @RequestParam String query,

            @Parameter(description = "Filter by difficulty")
            @RequestParam(required = false) Difficulty difficulty,

            @Parameter(description = "Filter by category ID")
            @RequestParam(required = false) Long categoryId,

            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Searching problems - query: {}, difficulty: {}, categoryId: {}",
                query, difficulty, categoryId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<ProblemSummaryResponse> response = problemService.searchProblems(
                query, difficulty, categoryId, pageable
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get random problems",
            description = "Get random problems for practice"
    )
    @ApiResponse(responseCode = "200", description = "Random problems retrieved successfully")
    @GetMapping("/random")
    public ResponseEntity<List<ProblemSummaryResponse>> getRandomProblems(
            @Parameter(description = "Number of problems to return")
            @RequestParam(defaultValue = "5") int count,

            @Parameter(description = "Filter by difficulty")
            @RequestParam(required = false) Difficulty difficulty,

            @Parameter(description = "Filter by category ID")
            @RequestParam(required = false) Long categoryId
    ) {
        log.info("Getting random problems - count: {}, difficulty: {}, categoryId: {}",
                count, difficulty, categoryId);

        List<ProblemSummaryResponse> response = problemService.getRandomProblems(
                count, difficulty, categoryId
        );

        return ResponseEntity.ok(response);
    }
}