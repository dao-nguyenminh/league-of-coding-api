package com.leagueofcoding.api.entity;

import com.leagueofcoding.api.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Match entity representing a coding battle between two players.
 * Contains match metadata, status, and timing information.
 *
 * @author dao-nguyenminh
 */
@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * First player ID. Foreign key to users table.
     */
    @Column(name = "player1_id", nullable = false)
    private Long player1Id;

    /**
     * Second player ID. Foreign key to users table.
     */
    @Column(name = "player2_id", nullable = false)
    private Long player2Id;

    /**
     * Problem ID for this match. Foreign key to problems table.
     */
    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    /**
     * Current match status. Defaults to WAITING.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MatchStatus status = MatchStatus.WAITING;

    /**
     * Winner user ID. Null if match not completed or canceled.
     */
    @Column(name = "winner_id")
    private Long winnerId;

    /**
     * Timestamp when match actually started (players joined).
     */
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    /**
     * Timestamp when match ended (completed or canceled).
     */
    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}