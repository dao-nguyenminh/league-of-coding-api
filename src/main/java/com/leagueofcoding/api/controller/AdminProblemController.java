package com.leagueofcoding.api.controller;

import com.leagueofcoding.api.dto.problem.*;
import com.leagueofcoding.api.entity.User;
import com.leagueofcoding.api.exception.UserNotFoundException;
import com.leagueofcoding.api.repository.UserRepository;
import com.leagueofcoding.api.security.UserPrincipal;
import com.leagueofcoding.api.service.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * AdminProblemController - Admin endpoints cho problem management.
 *
 * @author dao-nguyenminh
 */
@RestController
@RequestMapping("/api/admin/problems")
@RequiredArgsConstructor
@Tag(name = "Admin - Problems", description = "Problem management APIs (Admin only)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProblemController {

    private final ProblemService problemService;
    private final UserRepository userRepository;

    @Operation(
            summary = "Create new problem",
            description = "Create a new coding problem with test cases. Admin only."
    )
    @ApiResponse(responseCode = "201", description = "Problem created successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @PostMapping
    public ResponseEntity<ProblemResponse> createProblem(
            @Valid @RequestBody CreateProblemRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ProblemResponse response = problemService.createProblem(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update problem",
            description = "Update existing problem. Admin only."
    )
    @ApiResponse(responseCode = "200", description = "Problem updated successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    @ApiResponse(responseCode = "404", description = "Problem or category not found")
    @PutMapping("/{id}")
    public ResponseEntity<ProblemResponse> updateProblem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProblemRequest request
    ) {
        ProblemResponse response = problemService.updateProblem(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete problem",
            description = "Soft delete problem (set inactive). Admin only."
    )
    @ApiResponse(responseCode = "204", description = "Problem deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    @ApiResponse(responseCode = "404", description = "Problem not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add test case to problem",
            description = "Add a new test case to existing problem. Admin only."
    )
    @ApiResponse(responseCode = "201", description = "Test case created successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    @ApiResponse(responseCode = "404", description = "Problem not found")
    @PostMapping("/{problemId}/test-cases")
    public ResponseEntity<TestCaseResponse> addTestCase(
            @PathVariable Long problemId,
            @Valid @RequestBody TestCaseRequest request
    ) {
        TestCaseResponse response = problemService.addTestCase(problemId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Delete test case",
            description = "Delete a test case. Admin only."
    )
    @ApiResponse(responseCode = "204", description = "Test case deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    @ApiResponse(responseCode = "404", description = "Test case not found")
    @DeleteMapping("/test-cases/{id}")
    public ResponseEntity<Void> deleteTestCase(@PathVariable Long id) {
        problemService.deleteTestCase(id);
        return ResponseEntity.noContent().build();
    }
}