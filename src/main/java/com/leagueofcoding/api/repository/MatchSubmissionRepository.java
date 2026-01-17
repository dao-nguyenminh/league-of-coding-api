package com.leagueofcoding.api.repository;

import com.leagueofcoding.api.entity.MatchSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for MatchSubmission entity.
 *
 * @author dao-nguyenminh
 */
public interface MatchSubmissionRepository extends JpaRepository<MatchSubmission, Long> {

    /**
     * Find submission by match ID and user ID.
     *
     * @param matchId match ID
     * @param userId  user ID
     * @return Optional MatchSubmission
     */
    Optional<MatchSubmission> findByMatchIdAndUserId(Long matchId, Long userId);
}