package com.leagueofcoding.api.repository;

import com.leagueofcoding.api.entity.Match;
import com.leagueofcoding.api.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for Match entity.
 *
 * @author dao-nguyenminh
 */
public interface MatchRepository extends JpaRepository<Match, Long> {

    /**
     * Find all matches for a specific player (either player1 or player2).
     * Results ordered by creation date descending.
     *
     * @param player1Id first player ID
     * @param player2Id second player ID
     * @return list of matches
     */
    List<Match> findByPlayer1IdOrPlayer2IdOrderByCreatedAtDesc(Long player1Id, Long player2Id);

    /**
     * Find matches by status.
     *
     * @param status match status
     * @return list of matches
     */
    List<Match> findByStatus(MatchStatus status);
}