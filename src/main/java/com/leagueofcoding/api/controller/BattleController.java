package com.leagueofcoding.api.controller;

import com.leagueofcoding.api.entity.Match;
import com.leagueofcoding.api.entity.MatchSubmission;
import com.leagueofcoding.api.entity.User;
import com.leagueofcoding.api.repository.UserRepository;
import com.leagueofcoding.api.service.BattleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Battle system REST API controller.
 *
 * @author dao-nguyenminh
 */
@RestController
@RequestMapping("/api/battles")
@RequiredArgsConstructor
public class BattleController {

    private final BattleService battleService;
    private final UserRepository userRepository;

    /**
     * Join match room.
     *
     * @param matchId     match ID
     * @param userDetails authenticated user
     * @return match details
     */
    @PostMapping("/{matchId}/join")
    public ResponseEntity<Match> joinMatch(
            @PathVariable Long matchId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Match match = battleService.joinMatch(matchId, user.getId());
        return ResponseEntity.ok(match);
    }

    /**
     * Submit code for match.
     *
     * @param matchId     match ID
     * @param request     submission request
     * @param userDetails authenticated user
     * @return submission record
     */
    @PostMapping("/{matchId}/submit")
    public ResponseEntity<MatchSubmission> submitCode(
            @PathVariable Long matchId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String code = request.get("code");
        String language = request.get("language");

        MatchSubmission submission = battleService.submitCode(matchId, user.getId(), code, language);
        return ResponseEntity.ok(submission);
    }

    /**
     * Get match details.
     *
     * @param matchId     match ID
     * @param userDetails authenticated user
     * @return match details with submissions
     */
    @GetMapping("/{matchId}")
    public ResponseEntity<Map<String, Object>> getMatchDetails(
            @PathVariable Long matchId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> details = battleService.getMatchDetails(matchId, user.getId());
        return ResponseEntity.ok(details);
    }
}