package com.leagueofcoding.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Queue user data for Redis storage.
 *
 * @author dao-nguyenminh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueUserData {
    private Long userId;
    private Integer rating;
    private Long joinedAt;
}