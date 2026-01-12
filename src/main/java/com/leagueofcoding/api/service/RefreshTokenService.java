package com.leagueofcoding.api.service;

import com.leagueofcoding.api.entity.RefreshToken;
import com.leagueofcoding.api.entity.User;
import com.leagueofcoding.api.exception.InvalidRefreshTokenException;
import com.leagueofcoding.api.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RefreshTokenService - Business logic cho refresh tokens.
 *
 * @author dao-nguyenminh
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    /**
     * Create refresh token cho user.
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Revoke existing refresh token (1 user chỉ có 1 active refresh token)
        refreshTokenRepository.findByUser(user).ifPresent(existingToken -> {
            existingToken.setRevoked(true);
            refreshTokenRepository.save(existingToken);
        });

        // Create new refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Created refresh token for user: {}", user.getId());

        return refreshToken;
    }

    /**
     * Validate refresh token và return user.
     */
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

        if (refreshToken.getRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            throw new InvalidRefreshTokenException("Refresh token has expired");
        }

        return refreshToken;
    }

    /**
     * Revoke refresh token (logout).
     */
    @Transactional
    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        log.info("Revoked refresh token for user: {}", refreshToken.getUser().getId());
    }

    /**
     * Revoke tất cả refresh tokens của user.
     */
    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.deleteByUser(user);
        log.info("Revoked all refresh tokens for user: {}", user.getId());
    }

    /**
     * Cleanup expired tokens (scheduled task sẽ gọi method này).
     */
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteAllExpiredTokens(LocalDateTime.now());
        log.info("Cleaned up expired refresh tokens");
    }
}