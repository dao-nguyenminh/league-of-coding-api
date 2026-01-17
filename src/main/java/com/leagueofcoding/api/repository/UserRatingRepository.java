package com.leagueofcoding.api.repository;

import com.leagueofcoding.api.entity.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for UserRating entity.
 *
 * @author dao-nguyenminh
 */
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {

    /**
     * Find user rating by user ID.
     *
     * @param userId user ID
     * @return Optional UserRating
     */
    Optional<UserRating> findByUserId(Long userId);
}