package com.leagueofcoding.api.controller;

import com.leagueofcoding.api.service.OnlineUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * OnlineUsersController - REST API cho online users.
 *
 * @author dao-nguyenminh
 */
@RestController
@RequestMapping("/api/users/online")
@RequiredArgsConstructor
public class OnlineUsersController {

    private final OnlineUsersService onlineUsersService;

    /**
     * GET /api/users/online - Lấy danh sách users đang online.
     *
     * @return Set of online usernames
     */
    @GetMapping
    public ResponseEntity<Set<String>> getOnlineUsers() {
        Set<String> onlineUsers = onlineUsersService.getOnlineUsers();
        return ResponseEntity.ok(onlineUsers);
    }

    /**
     * GET /api/users/online/count - Đếm số users đang online.
     *
     * @return Count of online users
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getOnlineCount() {  // ← LONG, not INT!
        long count = onlineUsersService.getOnlineCount();  // ← long
        return ResponseEntity.ok(count);
    }

    /**
     * GET /api/users/online/{username} - Check user có online không.
     *
     * @param username Username to check
     * @return true if online, false otherwise
     */
    @GetMapping("/{username}")
    public ResponseEntity<Boolean> isUserOnline(@PathVariable String username) {
        boolean isOnline = onlineUsersService.isUserOnline(username);
        return ResponseEntity.ok(isOnline);
    }
}