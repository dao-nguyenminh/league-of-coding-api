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
     * Find all test cases for a problem.
     */
    @Query("SELECT t FROM TestCase t WHERE t.problem.id = :problemId ORDER BY t.orderIndex, t.id")
    List<TestCase> findByProblemId(@Param("problemId") Long problemId);

    /**
     * Find sample test cases for a problem (visible to users).
     */
    @Query("SELECT t FROM TestCase t WHERE t.problem.id = :problemId " +
            "AND t.isSample = true ORDER BY t.orderIndex, t.id")
    List<TestCase> findSampleTestCasesByProblemId(@Param("problemId") Long problemId);

    /**
     * Count test cases for a problem.
     */
    long countByProblemId(Long problemId);
}