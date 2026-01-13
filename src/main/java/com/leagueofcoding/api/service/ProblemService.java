package com.leagueofcoding.api.service;

import com.leagueofcoding.api.dto.problem.*;
import com.leagueofcoding.api.entity.Category;
import com.leagueofcoding.api.entity.Problem;
import com.leagueofcoding.api.entity.TestCase;
import com.leagueofcoding.api.entity.User;
import com.leagueofcoding.api.enums.Difficulty;
import com.leagueofcoding.api.exception.CategoryNotFoundException;
import com.leagueofcoding.api.exception.ProblemNotFoundException;
import com.leagueofcoding.api.exception.SlugAlreadyExistsException;
import com.leagueofcoding.api.repository.CategoryRepository;
import com.leagueofcoding.api.repository.ProblemRepository;
import com.leagueofcoding.api.repository.TestCaseRepository;
import com.leagueofcoding.api.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProblemService - Business logic cho problems.
 *
 * @author dao-nguyenminh
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final CategoryRepository categoryRepository;
    private final TestCaseRepository testCaseRepository;

    /**
     * Create new problem (Admin only).
     */
    @Transactional
    public ProblemResponse createProblem(CreateProblemRequest request, User creator) {
        log.info("Creating new problem: {} by user: {}", request.title(), creator.getId());

        // Validate category exists
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category not found with id: " + request.categoryId()
                ));

        // Generate unique slug
        String baseSlug = SlugUtils.generateSlug(request.title());
        String slug = generateUniqueSlug(baseSlug);

        // Create problem
        Problem problem = Problem.builder()
                .title(request.title())
                .slug(slug)
                .description(request.description())
                .difficulty(request.difficulty())
                .timeLimitMs(request.timeLimitMs() != null ? request.timeLimitMs() : 2000)
                .memoryLimitMb(request.memoryLimitMb() != null ? request.memoryLimitMb() : 256)
                .category(category)
                .createdBy(creator)
                .isActive(true)
                .build();

        problem = problemRepository.save(problem);
        log.info("Problem created with id: {}", problem.getId());

        // Create test cases if provided
        if (request.testCases() != null && !request.testCases().isEmpty()) {
            List<TestCase> testCases = createTestCasesForProblem(problem, request.testCases());
            problem.setTestCases(testCases);
        }

        // Get sample test cases for response
        List<TestCaseResponse> sampleTestCases = testCaseRepository
                .findSampleTestCasesByProblemId(problem.getId())
                .stream()
                .map(TestCaseResponse::from)
                .collect(Collectors.toList());

        return ProblemResponse.from(problem, sampleTestCases);
    }

    /**
     * Update problem (Admin only).
     */
    @Transactional
    public ProblemResponse updateProblem(Long problemId, UpdateProblemRequest request) {
        log.info("Updating problem id: {}", problemId);

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ProblemNotFoundException(
                        "Problem not found with id: " + problemId
                ));

        // Update fields if provided
        if (request.title() != null) {
            problem.setTitle(request.title());
        }

        if (request.description() != null) {
            problem.setDescription(request.description());
        }

        if (request.difficulty() != null) {
            problem.setDifficulty(request.difficulty());
        }

        if (request.timeLimitMs() != null) {
            problem.setTimeLimitMs(request.timeLimitMs());
        }

        if (request.memoryLimitMb() != null) {
            problem.setMemoryLimitMb(request.memoryLimitMb());
        }

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(
                            "Category not found with id: " + request.categoryId()
                    ));
            problem.setCategory(category);
        }

        if (request.isActive() != null) {
            problem.setIsActive(request.isActive());
        }

        problem = problemRepository.save(problem);
        log.info("Problem updated: {}", problem.getId());

        // Get sample test cases for response
        List<TestCaseResponse> sampleTestCases = testCaseRepository
                .findSampleTestCasesByProblemId(problem.getId())
                .stream()
                .map(TestCaseResponse::from)
                .collect(Collectors.toList());

        return ProblemResponse.from(problem, sampleTestCases);
    }

    /**
     * Get problem by slug (public access).
     */
    @Transactional(readOnly = true)
    public ProblemResponse getProblemBySlug(String slug) {
        log.info("Getting problem by slug: {}", slug);

        Problem problem = problemRepository.findBySlugAndActive(slug)
                .orElseThrow(() -> new ProblemNotFoundException(
                        "Problem not found with slug: " + slug
                ));

        // Get sample test cases only (hidden test cases not exposed)
        List<TestCaseResponse> sampleTestCases = testCaseRepository
                .findSampleTestCasesByProblemId(problem.getId())
                .stream()
                .map(TestCaseResponse::from)
                .collect(Collectors.toList());

        return ProblemResponse.from(problem, sampleTestCases);
    }

    /**
     * List problems with filtering and pagination.
     */
    @Transactional(readOnly = true)
    public Page<ProblemSummaryResponse> listProblems(Difficulty difficulty, Long categoryId, Pageable pageable) {
        log.info("Listing problems - difficulty: {}, categoryId: {}", difficulty, categoryId);

        Page<Problem> problems;

        if (difficulty != null && categoryId != null) {
            problems = problemRepository.findByCategoryAndDifficultyAndActive(
                    categoryId, difficulty, pageable
            );
        } else if (difficulty != null) {
            problems = problemRepository.findByDifficultyAndActive(difficulty, pageable);
        } else if (categoryId != null) {
            problems = problemRepository.findByCategoryAndActive(categoryId, pageable);
        } else {
            problems = problemRepository.findByIsActiveTrue(pageable);
        }

        return problems.map(ProblemSummaryResponse::from);
    }

    /**
     * Search problems by query with filters.
     */
    @Transactional(readOnly = true)
    public Page<ProblemSummaryResponse> searchProblems(String query, Difficulty difficulty, Long categoryId, Pageable pageable) {
        log.info("Searching problems - query: {}, difficulty: {}, categoryId: {}", query, difficulty, categoryId);

        Page<Problem> problems;

        if (difficulty != null && categoryId != null) {
            problems = problemRepository.searchByQueryAndCategoryAndDifficultyAndActive(
                    query, categoryId, difficulty, pageable
            );
        } else if (difficulty != null) {
            problems = problemRepository.searchByQueryAndDifficultyAndActive(query, difficulty, pageable);
        } else if (categoryId != null) {
            problems = problemRepository.searchByQueryAndCategoryAndActive(query, categoryId, pageable);
        } else {
            problems = problemRepository.searchByQueryAndActive(query, pageable);
        }

        return problems.map(ProblemSummaryResponse::from);
    }

    /**
     * Get random problems for practice.
     */
    @Transactional(readOnly = true)
    public List<ProblemSummaryResponse> getRandomProblems(int count, Difficulty difficulty, Long categoryId) {
        log.info("Getting random problems - count: {}, difficulty: {}, categoryId: {}", count, difficulty, categoryId);

        List<Problem> problems;

        if (difficulty != null && categoryId != null) {
            problems = problemRepository.findRandomByCategoryAndDifficultyAndActive(categoryId, difficulty.name(), count);
        } else if (difficulty != null) {
            problems = problemRepository.findRandomByDifficultyAndActive(difficulty.name(), count);
        } else if (categoryId != null) {
            problems = problemRepository.findRandomByCategoryAndActive(categoryId, count);
        } else {
            problems = problemRepository.findRandomByActive(count);
        }

        return problems.stream()
                .map(ProblemSummaryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Delete problem (Admin only - soft delete).
     */
    @Transactional
    public void deleteProblem(Long problemId) {
        log.info("Deleting (deactivating) problem id: {}", problemId);

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ProblemNotFoundException(
                        "Problem not found with id: " + problemId
                ));

        problem.setIsActive(false);
        problemRepository.save(problem);

        log.info("Problem deactivated: {}", problemId);
    }

    /**
     * Add test case to problem.
     */
    @Transactional
    public TestCaseResponse addTestCase(Long problemId, TestCaseRequest request) {
        log.info("Adding test case to problem: {}", problemId);

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ProblemNotFoundException(
                        "Problem not found with id: " + problemId
                ));

        TestCase testCase = TestCase.builder()
                .problem(problem)
                .input(request.input())
                .expectedOutput(request.expectedOutput())
                .isSample(request.isSample())
                .orderIndex(request.orderIndex() != null ? request.orderIndex() : 0)
                .build();

        testCase = testCaseRepository.save(testCase);
        log.info("Test case created with id: {}", testCase.getId());

        return TestCaseResponse.from(testCase);
    }

    /**
     * Delete test case.
     */
    @Transactional
    public void deleteTestCase(Long testCaseId) {
        log.info("Deleting test case: {}", testCaseId);

        if (!testCaseRepository.existsById(testCaseId)) {
            throw new ProblemNotFoundException("Test case not found with id: " + testCaseId);
        }

        testCaseRepository.deleteById(testCaseId);
        log.info("Test case deleted: {}", testCaseId);
    }

    /**
     * Generate unique slug.
     */
    private String generateUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int attempt = 0;

        while (problemRepository.existsBySlug(slug)) {
            attempt++;
            slug = SlugUtils.generateUniqueSlug(baseSlug, attempt);

            if (attempt > 100) {
                throw new SlugAlreadyExistsException(
                        "Cannot generate unique slug for: " + baseSlug
                );
            }
        }

        return slug;
    }

    /**
     * Create test cases for problem.
     */
    private List<TestCase> createTestCasesForProblem(Problem problem, List<TestCaseRequest> requests) {
        return requests.stream()
                .map(req -> TestCase.builder()
                        .problem(problem)
                        .input(req.input())
                        .expectedOutput(req.expectedOutput())
                        .isSample(req.isSample())
                        .orderIndex(req.orderIndex() != null ? req.orderIndex() : 0)
                        .build())
                .map(testCaseRepository::save)
                .collect(Collectors.toList());
    }
}