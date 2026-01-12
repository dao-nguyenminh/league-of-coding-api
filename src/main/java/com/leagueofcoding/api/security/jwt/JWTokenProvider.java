package com.leagueofcoding.api.security.jwt;

import com.leagueofcoding.api.security.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Token Provider - Generate và validate JWT tokens.
 *
 * <p>Sử dụng HS256 algorithm, token expiration configurable qua application.yml.
 *
 * @author dao-nguyenminh
 */
@Component
@Slf4j
public class JWTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Generate JWT token từ authenticated user.
     *
     * @param authentication Spring Security Authentication object
     * @return JWT token string (format: header.payload.signature)
     */
    public String generateToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        Long userId = extractUserId(principal);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Extract user ID từ JWT token.
     *
     * @param token JWT token string
     * @return User ID
     * @throws JwtException nếu token invalid
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Validate JWT token (signature, expiration, format).
     *
     * @param token JWT token string
     * @return true nếu token valid, false nếu invalid
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims empty: {}", ex.getMessage());
        }

        return false;
    }

    /**
     * Generate SecretKey từ secret string cho HS256 algorithm.
     *
     * @return SecretKey object
     */
    private SecretKey getSecretKey() {
        byte[] bytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }

    /**
     * Extract user ID từ authentication principal.
     *
     * @param principal authentication principal object
     * @return User ID
     */
    private Long extractUserId(Object principal) {
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getId();
        }
        throw new IllegalArgumentException("Invalid principal type");
    }

    /**
     * Extract tất cả claims từ JWT token.
     *
     * @param token JWT token string
     * @return Claims object chứa payload
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}