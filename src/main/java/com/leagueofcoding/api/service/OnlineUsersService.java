package com.leagueofcoding.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OnlineUsersService - Track online users.
 *
 * @author dao-nguyenminh
 */
@Slf4j
@Service
public class OnlineUsersService {

    // Thread-safe set of online usernames
    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    /**
     * Add user to online list.
     */
    public void addUser(String username) {
        onlineUsers.add(username);
        log.info("User connected: {} (Total online: {})", username, onlineUsers.size());
    }

    /**
     * Remove user from online list.
     */
    public void removeUser(String username) {
        onlineUsers.remove(username);
        log.info("User disconnected: {} (Total online: {})", username, onlineUsers.size());
    }

    /**
     * Check if user is online.
     */
    public boolean isUserOnline(String username) {
        return onlineUsers.contains(username);
    }

    /**
     * Get all online users.
     */
    public Set<String> getOnlineUsers() {
        return Set.copyOf(onlineUsers);
    }

    /**
     * Get online user count.
     */
    public long getOnlineCount() {
        return onlineUsers.size();
    }
}