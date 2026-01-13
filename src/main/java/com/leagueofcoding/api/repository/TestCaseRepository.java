package com.leagueofcoding.api.repository;

import com.leagueofcoding.api.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TestCaseRepository - Data access cho test cases.
 *
 * @author dao-nguyenminh
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    /**
     * Find sample test cases by problem ID (visible to users).
     */
    @Query("SELECT tc FROM TestCase tc WHERE tc.problem.id = :problemId AND tc.isSample = true ORDER BY tc.orderIndex")
    List<TestCase> findSampleTestCasesByProblemId(@Param("problemId") Long problemId);

    /**
     * Find all test cases by problem ID (admin only).
     */
    @Query("SELECT tc FROM TestCase tc WHERE tc.problem.id = :problemId ORDER BY tc.isSample DESC, tc.orderIndex")
    List<TestCase> findAllTestCasesByProblemId(@Param("problemId") Long problemId);

    /**
     * Count test cases by problem ID.
     */
    @Query("SELECT COUNT(tc) FROM TestCase tc WHERE tc.problem.id = :problemId")
    long countByProblemId(@Param("problemId") Long problemId);

    /**
     * Count sample test cases by problem ID.
     */
    @Query("SELECT COUNT(tc) FROM TestCase tc WHERE tc.problem.id = :problemId AND tc.isSample = true")
    long countSampleTestCasesByProblemId(@Param("problemId") Long problemId);
}