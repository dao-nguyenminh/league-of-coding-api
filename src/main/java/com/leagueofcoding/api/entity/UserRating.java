package com.leagueofcoding.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * User ELO rating entity for matchmaking system.
 * Tracks user's rating, match statistics, and win/loss records.
 *
 * @author dao-nguyenminh
 */
@Entity
@Table(name = "user_ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Reference to user ID. One-to-one relationship.
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    /**
     * ELO rating score. Default starting rating is 1200.
     */
    @Column(name = "elo_rating", nullable = false)
    @Builder.Default
    private Integer eloRating = 1200;

    /**
     * Total number of matches played (wins + losses).
     */
    @Column(name = "matches_played", nullable = false)
    @Builder.Default
    private Integer matchesPlayed = 0;

    /**
     * Total number of wins.
     */
    @Column(name = "wins", nullable = false)
    @Builder.Default
    private Integer wins = 0;

    /**
     * Total number of losses.
     */
    @Column(name = "losses", nullable = false)
    @Builder.Default
    private Integer losses = 0;

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

    /**
     * Calculate win rate percentage.
     *
     * @return win rate as percentage (0-100)
     */
    public double getWinRate() {
        if (matchesPlayed == 0) {
            return 0.0;
        }
        return (wins * 100.0) / matchesPlayed;
    }
}