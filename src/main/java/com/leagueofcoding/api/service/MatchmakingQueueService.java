package com.leagueofcoding.api.service;

import com.leagueofcoding.api.dto.QueueUserData;
import com.leagueofcoding.api.entity.Match;
import com.leagueofcoding.api.entity.Problem;
import com.leagueofcoding.api.entity.UserRating;
import com.leagueofcoding.api.enums.MatchStatus;
import com.leagueofcoding.api.repository.MatchRepository;
import com.leagueofcoding.api.repository.ProblemRepository;
import com.leagueofcoding.api.repository.UserRatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Matchmaking queue service using Redis.
 * Handles player queue, ELO-based matching, and match creation.
 *
 * @author dao-nguyenminh
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchmakingQueueService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRatingRepository userRatingRepository;
    private final MatchRepository matchRepository;
    private final ProblemRepository problemRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String QUEUE_KEY = "matchmaking:queue";
    private static final String USER_QUEUE_KEY = "matchmaking:user:%d";
    private static final int RATING_TOLERANCE = 200; // ELO difference tolerance
    private static final int QUEUE_TIMEOUT_SECONDS = 300; // 5 minutes

    /**
     * Add user to matchmaking queue.
     * Stores user ID with timestamp and rating.
     *
     * @param userId user ID to add
     * @return true if added successfully
     */
    public boolean joinQueue(Long userId) {
        // Check if already in queue
        if (isInQueue(userId)) {
            log.warn("User {} already in queue", userId);
            return false;
        }

        // Get user rating (or create default)
        UserRating rating = userRatingRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultRating(userId));

        // Store in Redis sorted set (score = ELO rating for efficient matching)
        String userKey = String.format(USER_QUEUE_KEY, userId);
        QueueUserData queueData = new QueueUserData(
                userId,
                rating.getEloRating(),
                System.currentTimeMillis()
        );

        redisTemplate.opsForZSet().add(QUEUE_KEY, userId, rating.getEloRating());
        redisTemplate.opsForValue().set(userKey, queueData, QUEUE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        log.info("User {} joined queue (rating: {})", userId, rating.getEloRating());

        // Try to find match
        tryMatchmaking(userId, rating.getEloRating());

        return true;
    }

    /**
     * Remove user from queue.
     *
     * @param userId user ID to remove
     */
    public void leaveQueue(Long userId) {
        String userKey = String.format(USER_QUEUE_KEY, userId);

        redisTemplate.opsForZSet().remove(QUEUE_KEY, userId);
        redisTemplate.delete(userKey);

        log.info("User {} left queue", userId);
    }

    /**
     * Check if user is in queue.
     *
     * @param userId user ID
     * @return true if in queue
     */
    public boolean isInQueue(Long userId) {
        String userKey = String.format(USER_QUEUE_KEY, userId);
        return redisTemplate.hasKey(userKey);
    }

    /**
     * Get current queue size.
     *
     * @return number of users in queue
     */
    public long getQueueSize() {
        Long size = redisTemplate.opsForZSet().size(QUEUE_KEY);
        return size != null ? size : 0;
    }

    /**
     * Try to find a match for user.
     * Uses ELO-based matching algorithm.
     *
     * @param userId     user ID
     * @param userRating user's ELO rating
     */
    private void tryMatchmaking(Long userId, Integer userRating) {
        // Find players with similar rating (within tolerance)
        Set<Object> candidates = redisTemplate.opsForZSet().rangeByScore(
                QUEUE_KEY,
                userRating - RATING_TOLERANCE,
                userRating + RATING_TOLERANCE
        );

        if (candidates == null || candidates.size() < 2) {
            log.debug("Not enough players for matching (queue size: {})", candidates != null ? candidates.size() : 0);
            return;
        }

        // Find best match (closest rating, excluding self)
        Long opponentId = null;
        int minDiff = Integer.MAX_VALUE;

        for (Object candidate : candidates) {
            Long candidateId = Long.valueOf(candidate.toString());
            if (candidateId.equals(userId)) continue;

            String candidateKey = String.format(USER_QUEUE_KEY, candidateId);
            QueueUserData candidateData = (QueueUserData) redisTemplate.opsForValue().get(candidateKey);

            if (candidateData != null) {
                Integer candidateRating = candidateData.getRating();
                int diff = Math.abs(userRating - candidateRating);

                if (diff < minDiff) {
                    minDiff = diff;
                    opponentId = candidateId;
                }
            }
        }

        if (opponentId != null) {
            createMatch(userId, opponentId);
        }
    }

    /**
     * Create match between two players.
     * Removes both from queue and notifies via WebSocket.
     *
     * @param player1Id first player ID
     * @param player2Id second player ID
     */
    private void createMatch(Long player1Id, Long player2Id) {
        // Remove both from queue
        leaveQueue(player1Id);
        leaveQueue(player2Id);

        // Select random problem
        Long problemId = selectRandomProblem();

        // Create match entity
        Match match = Match.builder()
                .player1Id(player1Id)
                .player2Id(player2Id)
                .problemId(problemId)
                .status(MatchStatus.WAITING)
                .build();

        match = matchRepository.save(match);

        log.info("Match created: {} vs {} (Match ID: {}, Problem ID: {})",
                player1Id, player2Id, match.getId(), problemId);

        // Notify both players via WebSocket
        notifyMatchFound(player1Id, player2Id, match.getId());
    }

    /**
     * Notify players that match was found.
     *
     * @param player1Id first player ID
     * @param player2Id second player ID
     * @param matchId   created match ID
     */
    private void notifyMatchFound(Long player1Id, Long player2Id, Long matchId) {
        Map<String, Object> notification = Map.of(
                "type", "MATCH_FOUND",
                "matchId", matchId,
                "message", "Match found! Redirecting to battle...",
                "timestamp", LocalDateTime.now()
        );

        // Send to both players
        messagingTemplate.convertAndSendToUser(
                player1Id.toString(),
                "/queue/match-found",
                notification
        );

        messagingTemplate.convertAndSendToUser(
                player2Id.toString(),
                "/queue/match-found",
                notification
        );

        log.info("Match found notifications sent to players {} and {}", player1Id, player2Id);
    }

    /**
     * Select random problem for match.
     *
     * @return random problem ID
     */
    private Long selectRandomProblem() {
        List<Long> problemIds = problemRepository.findAll().stream()
                .map(Problem::getId)
                .toList();

        if (problemIds.isEmpty()) {
            throw new RuntimeException("No problems available for matching");
        }

        return problemIds.get(new Random().nextInt(problemIds.size()));
    }

    /**
     * Create default rating for new user.
     *
     * @param userId user ID
     * @return created UserRating
     */
    private UserRating createDefaultRating(Long userId) {
        UserRating rating = UserRating.builder()
                .userId(userId)
                .eloRating(1200)
                .matchesPlayed(0)
                .wins(0)
                .losses(0)
                .build();

        return userRatingRepository.save(rating);
    }
}