package com.leagueofcoding.api.repository;

import com.leagueofcoding.api.entity.RefreshToken;
import com.leagueofcoding.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * RefreshTokenRepository - Data access cho refresh tokens.
 *
 * @author dao-nguyenminh
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Find refresh token by token string.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Find all refresh tokens của user.
     */
    Optional<RefreshToken> findByUser(User user);

    /**
     * Delete all refresh tokens của user (dùng khi logout).
     */
    void deleteByUser(User user);

    /**
     * Delete all expired refresh tokens (cleanup job).
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    void deleteAllExpiredTokens(LocalDateTime now);
}