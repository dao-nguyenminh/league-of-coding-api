package com.leagueofcoding.api.enums;

/**
 * Match status enumeration.
 *
 * @author dao-nguyenminh
 */
public enum MatchStatus {
    /**
     * Match created, waiting for players to join
     */
    WAITING,

    /**
     * Battle in progress
     */
    IN_PROGRESS,

    /**
     * Battle finished, winner determined
     */
    COMPLETED,

    /**
     * Match canceled (timeout, player left, etc.)
     */
    CANCELLED
}