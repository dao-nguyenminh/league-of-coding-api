package com.leagueofcoding.api.controller;

import com.leagueofcoding.api.dto.problem.CategoryResponse;
import com.leagueofcoding.api.dto.problem.ProblemResponse;
import com.leagueofcoding.api.dto.problem.ProblemSummaryResponse;
import com.leagueofcoding.api.enums.Difficulty;
import com.leagueofcoding.api.service.CategoryService;
import com.leagueofcoding.api.service.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProblemController - Public endpoints cho problems.
 *
 * @author dao-nguyenminh
 */
@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
@Tag(name = "Problems", description = "Public problem browsing APIs")
public class ProblemController {

    private final ProblemService problemService;
    private final CategoryService categoryService;

    @Operation(
            summary = "List all problems",
            description = "Get paginated list of active problems with optional filtering by difficulty and category."
    )
    @ApiResponse(responseCode = "200", description = "Problems retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<ProblemSummaryResponse>> listProblems(
            @Parameter(description = "Filter by difficulty (EASY, MEDIUM, HARD)")
            @RequestParam(required = false) Difficulty difficulty,

            @Parameter(description = "Filter by category ID")
            @RequestParam(required = false) Long categoryId,

            @Parameter(description = "Pagination parameters (page, size, sort)")
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        Page<ProblemSummaryResponse> problems = problemService.listProblems(
                difficulty, categoryId, pageable
        );
        return ResponseEntity.ok(problems);
    }

    @Operation(
            summary = "Get problem by slug",
            description = "Get detailed problem information including sample test cases."
    )
    @ApiResponse(responseCode = "200", description = "Problem retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Problem not found")
    @GetMapping("/{slug}")
    public ResponseEntity<ProblemResponse> getProblem(@PathVariable String slug) {
        ProblemResponse problem = problemService.getProblemBySlug(slug);
        return ResponseEntity.ok(problem);
    }

    @Operation(
            summary = "List all categories",
            description = "Get all problem categories."
    )
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> listCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}