package com.leagueofcoding.api.controller;

import com.leagueofcoding.api.entity.User;
import com.leagueofcoding.api.repository.UserRepository;
import com.leagueofcoding.api.service.MatchmakingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Matchmaking REST API controller.
 *
 * @author dao-nguyenminh
 */
@RestController
@RequestMapping("/api/matchmaking")
@RequiredArgsConstructor
public class MatchmakingController {

    private final MatchmakingQueueService queueService;
    private final UserRepository userRepository;

    /**
     * Join matchmaking queue.
     *
     * @param userDetails authenticated user
     * @return success message with queue info
     */
    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> joinQueue(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean joined = queueService.joinQueue(user.getId());

        if (!joined) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Already in queue"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Joined matchmaking queue",
                "queueSize", queueService.getQueueSize()
        ));
    }

    /**
     * Leave matchmaking queue.
     *
     * @param userDetails authenticated user
     * @return success message
     */
    @PostMapping("/leave")
    public ResponseEntity<Map<String, String>> leaveQueue(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        queueService.leaveQueue(user.getId());

        return ResponseEntity.ok(Map.of(
                "message", "Left matchmaking queue"
        ));
    }

    /**
     * Get queue status for current user.
     *
     * @param userDetails authenticated user
     * @return queue status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(Map.of(
                "inQueue", queueService.isInQueue(user.getId()),
                "queueSize", queueService.getQueueSize()
        ));
    }
}