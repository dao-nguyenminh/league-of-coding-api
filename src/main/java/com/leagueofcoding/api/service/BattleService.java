package com.leagueofcoding.api.service;

import com.leagueofcoding.api.entity.Match;
import com.leagueofcoding.api.entity.MatchSubmission;
import com.leagueofcoding.api.enums.MatchStatus;
import com.leagueofcoding.api.enums.SubmissionStatus;
import com.leagueofcoding.api.repository.MatchRepository;
import com.leagueofcoding.api.repository.MatchSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Battle system service.
 * Handles match room, code submission, and battle state management.
 *
 * @author dao-nguyenminh
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BattleService {

    private final MatchRepository matchRepository;
    private final MatchSubmissionRepository submissionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final int BATTLE_DURATION_MINUTES = 15;

    /**
     * Join match room.
     * Start match when both players joined.
     *
     * @param matchId match ID
     * @param userId  user ID
     * @return match details
     */
    @Transactional
    public Match joinMatch(Long matchId, Long userId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // Verify user is participant
        if (!match.getPlayer1Id().equals(userId) && !match.getPlayer2Id().equals(userId)) {
            throw new RuntimeException("User not in this match");
        }

        // Start match if still waiting
        if (match.getStatus() == MatchStatus.WAITING) {
            match.setStatus(MatchStatus.IN_PROGRESS);
            match.setStartedAt(LocalDateTime.now());
            match = matchRepository.save(match);

            log.info("Match {} started", matchId);

            // Broadcast match started
            broadcastMatchStarted(match);
        }

        return match;
    }

    /**
     * Submit code for match.
     *
     * @param matchId  match ID
     * @param userId   user ID
     * @param code     source code
     * @param language programming language
     * @return submission record
     */
    @Transactional
    public MatchSubmission submitCode(Long matchId, Long userId, String code, String language) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // Verify match is in progress
        if (match.getStatus() != MatchStatus.IN_PROGRESS) {
            throw new RuntimeException("Match not in progress");
        }

        // Verify user is participant
        if (!match.getPlayer1Id().equals(userId) && !match.getPlayer2Id().equals(userId)) {
            throw new RuntimeException("User not in this match");
        }

        // Check if user already submitted
        submissionRepository.findByMatchIdAndUserId(matchId, userId)
                .ifPresent(s -> {
                    throw new RuntimeException("Already submitted");
                });

        // Create submission
        MatchSubmission submission = MatchSubmission.builder()
                .matchId(matchId)
                .userId(userId)
                .code(code)
                .language(com.leagueofcoding.api.enums.ProgrammingLanguage.valueOf(language))
                .status(SubmissionStatus.PENDING)
                .build();

        submission = submissionRepository.save(submission);

        log.info("User {} submitted code for match {}", userId, matchId);

        // Broadcast submission event
        broadcastSubmission(match, userId);

        // Mock judge (for now - will integrate Judge0 later)
        mockJudge(submission);

        return submission;
    }

    /**
     * Get match details with submissions.
     *
     * @param matchId match ID
     * @param userId  requesting user ID
     * @return match details
     */
    public Map<String, Object> getMatchDetails(Long matchId, Long userId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // Verify user is participant
        if (!match.getPlayer1Id().equals(userId) && !match.getPlayer2Id().equals(userId)) {
            throw new RuntimeException("User not in this match");
        }

        // Get submissions
        var player1Submission = submissionRepository.findByMatchIdAndUserId(matchId, match.getPlayer1Id());
        var player2Submission = submissionRepository.findByMatchIdAndUserId(matchId, match.getPlayer2Id());

        return Map.of(
                "match", match,
                "player1Submitted", player1Submission.isPresent(),
                "player2Submitted", player2Submission.isPresent(),
                "timeRemaining", calculateTimeRemaining(match)
        );
    }

    /**
     * Calculate time remaining in battle.
     *
     * @param match match entity
     * @return seconds remaining
     */
    private long calculateTimeRemaining(Match match) {
        if (match.getStatus() != MatchStatus.IN_PROGRESS || match.getStartedAt() == null) {
            return 0;
        }

        LocalDateTime endTime = match.getStartedAt().plusMinutes(BATTLE_DURATION_MINUTES);
        long secondsRemaining = java.time.Duration.between(LocalDateTime.now(), endTime).getSeconds();

        return Math.max(0, secondsRemaining);
    }

    /**
     * Mock judge - simulate code execution.
     * TODO: Replace with Judge0 integration.
     *
     * @param submission submission to judge
     */
    private void mockJudge(MatchSubmission submission) {
        // Simulate random pass/fail for testing
        boolean passed = Math.random() > 0.3; // 70% pass rate

        submission.setStatus(passed ? SubmissionStatus.PASSED : SubmissionStatus.FAILED);
        submission.setTestCasesPassed(passed ? 10 : (int) (Math.random() * 10));
        submission.setTestCasesTotal(10);
        submission.setExecutionTimeMs((int) (Math.random() * 1000));
        submission.setMemoryUsedKb((int) (Math.random() * 50000));
        submission.setJudgedAt(LocalDateTime.now());

        submissionRepository.save(submission);

        log.info("Submission {} judged: {}", submission.getId(), submission.getStatus());

        // Check if this determines winner
        if (passed) {
            checkWinner(submission.getMatchId(), submission.getUserId());
        }
    }

    /**
     * Check if submission determines match winner.
     *
     * @param matchId match ID
     * @param userId  user who passed
     */
    private void checkWinner(Long matchId, Long userId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        if (match.getStatus() != MatchStatus.IN_PROGRESS) {
            return;
        }

        // Set winner
        match.setWinnerId(userId);
        match.setStatus(MatchStatus.COMPLETED);
        match.setEndedAt(LocalDateTime.now());
        matchRepository.save(match);

        log.info("Match {} completed, winner: {}", matchId, userId);

        // Broadcast match ended
        broadcastMatchEnded(match, userId);
    }

    /**
     * Broadcast match started event.
     *
     * @param match match entity
     */
    private void broadcastMatchStarted(Match match) {
        Map<String, Object> event = Map.of(
                "type", "MATCH_STARTED",
                "matchId", match.getId(),
                "startedAt", match.getStartedAt(),
                "duration", BATTLE_DURATION_MINUTES
        );

        messagingTemplate.convertAndSendToUser(
                match.getPlayer1Id().toString(),
                "/queue/battle",
                event
        );

        messagingTemplate.convertAndSendToUser(
                match.getPlayer2Id().toString(),
                "/queue/battle",
                event
        );
    }

    /**
     * Broadcast submission event.
     *
     * @param match  match entity
     * @param userId user who submitted
     */
    private void broadcastSubmission(Match match, Long userId) {
        Map<String, Object> event = Map.of(
                "type", "OPPONENT_SUBMITTED",
                "matchId", match.getId(),
                "userId", userId
        );

        // Notify opponent
        Long opponentId = match.getPlayer1Id().equals(userId) ?
                match.getPlayer2Id() : match.getPlayer1Id();

        messagingTemplate.convertAndSendToUser(
                opponentId.toString(),
                "/queue/battle",
                event
        );
    }

    /**
     * Broadcast match ended event.
     *
     * @param match    match entity
     * @param winnerId winner user ID
     */
    private void broadcastMatchEnded(Match match, Long winnerId) {
        Map<String, Object> event = Map.of(
                "type", "MATCH_ENDED",
                "matchId", match.getId(),
                "winnerId", winnerId,
                "endedAt", match.getEndedAt()
        );

        messagingTemplate.convertAndSendToUser(
                match.getPlayer1Id().toString(),
                "/queue/battle",
                event
        );

        messagingTemplate.convertAndSendToUser(
                match.getPlayer2Id().toString(),
                "/queue/battle",
                event
        );
    }
}