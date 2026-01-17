package com.leagueofcoding.api.entity;

import com.leagueofcoding.api.enums.ProgrammingLanguage;
import com.leagueofcoding.api.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Code submission entity for matches.
 * Stores submitted code, language, execution results, and judge status.
 *
 * @author dao-nguyenminh
 */
@Entity
@Table(name = "match_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Match ID this submission belongs to.
     */
    @Column(name = "match_id", nullable = false)
    private Long matchId;

    /**
     * User ID who submitted the code.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Submitted source code.
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String code;

    /**
     * Programming language used.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgrammingLanguage language;

    /**
     * Submission judge status. Defaults to PENDING.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.PENDING;

    /**
     * Execution time in milliseconds.
     */
    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;

    /**
     * Memory used in kilobytes.
     */
    @Column(name = "memory_used_kb")
    private Integer memoryUsedKb;

    /**
     * Number of test cases that passed.
     */
    @Column(name = "test_cases_passed")
    private Integer testCasesPassed;

    /**
     * Total number of test cases.
     */
    @Column(name = "test_cases_total")
    private Integer testCasesTotal;

    /**
     * Timestamp when code was submitted.
     */
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    /**
     * Timestamp when judging completed.
     */
    @Column(name = "judged_at")
    private LocalDateTime judgedAt;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}