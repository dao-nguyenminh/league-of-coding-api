package com.leagueofcoding.api.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * RateLimitService - Token bucket based rate limiting.
 *
 * @author dao-nguyenminh
 */
@Slf4j
@Service
public class RateLimitService {

    private final Cache<String, Bucket> loginBuckets;
    private final Cache<String, Bucket> registerBuckets;

    public RateLimitService() {
        this.loginBuckets = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(1))
                .maximumSize(10000)
                .build();

        this.registerBuckets = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(10000)
                .build();
    }

    /**
     * Check rate limit cho login endpoint.
     */
    public boolean tryConsumeLogin(String key) {
        Bucket bucket = loginBuckets.get(key, k -> createLoginBucket());
        boolean consumed = bucket.tryConsume(1);

        if (!consumed) {
            log.warn("Rate limit exceeded for login: {}", key);
        }

        return consumed;
    }

    /**
     * Check rate limit cho registration endpoint.
     */
    public boolean tryConsumeRegister(String key) {
        Bucket bucket = registerBuckets.get(key, k -> createRegisterBucket());
        boolean consumed = bucket.tryConsume(1);

        if (!consumed) {
            log.warn("Rate limit exceeded for registration: {}", key);
        }

        return consumed;
    }

    /**
     * Tạo bucket cho login: 5 requests/minute.
     * Updated to use new Bucket4j 8.x API (non-deprecated).
     */
    private Bucket createLoginBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(5)
                .refillIntervally(5, Duration.ofMinutes(1))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Tạo bucket cho registration: 3 requests/hour.
     * Updated to use new Bucket4j 8.x API (non-deprecated).
     */
    private Bucket createRegisterBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(3)
                .refillIntervally(3, Duration.ofHours(1))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Get số requests còn lại cho login.
     */
    public long getLoginAvailableTokens(String key) {
        Bucket bucket = loginBuckets.getIfPresent(key);
        return bucket != null ? bucket.getAvailableTokens() : 5;
    }

    /**
     * Get số requests còn lại cho registration.
     */
    public long getRegisterAvailableTokens(String key) {
        Bucket bucket = registerBuckets.getIfPresent(key);
        return bucket != null ? bucket.getAvailableTokens() : 3;
    }
}