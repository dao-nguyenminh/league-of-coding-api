package com.leagueofcoding.api.service;

import com.leagueofcoding.api.dto.AuthResponse;
import com.leagueofcoding.api.dto.LoginRequest;
import com.leagueofcoding.api.dto.RegisterRequest;
import com.leagueofcoding.api.dto.UserResponse;
import com.leagueofcoding.api.entity.RefreshToken;
import com.leagueofcoding.api.entity.User;
import com.leagueofcoding.api.exception.EmailAlreadyExistsException;
import com.leagueofcoding.api.exception.RateLimitExceededException;
import com.leagueofcoding.api.exception.UsernameAlreadyExistsException;
import com.leagueofcoding.api.repository.UserRepository;
import com.leagueofcoding.api.security.RateLimitService;
import com.leagueofcoding.api.security.UserPrincipal;
import com.leagueofcoding.api.security.jwt.JWTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthService - Business logic cho authentication operations.
 *
 * @author dao-nguyenminh
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final RateLimitService rateLimitService;

    /**
     * Register user mới với rate limiting.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request, String clientIp) {
        log.info("Registering new user: {} from IP: {}", request.username(), clientIp);

        // Check rate limit
        if (!rateLimitService.tryConsumeRegister(clientIp)) {
            throw new RateLimitExceededException(
                    "Too many registration attempts. Please try again later.",
                    3600 // 1 hour
            );
        }

        // Check uniqueness
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(
                    "Username '" + request.username() + "' is already taken"
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(
                    "Email '" + request.email() + "' is already registered"
            );
        }

        // Create user
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .rating(1000)
                .totalMatches(0)
                .wins(0)
                .losses(0)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getId());

        // Generate tokens
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
        );

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                UserResponse.from(user)
        );
    }

    /**
     * Login user với rate limiting.
     */
    @Transactional
    public AuthResponse login(LoginRequest request, String clientIp) {
        log.info("User login attempt: {} from IP: {}", request.username(), clientIp);

        // Check rate limit
        if (!rateLimitService.tryConsumeLogin(clientIp)) {
            throw new RateLimitExceededException(
                    "Too many login attempts. Please try again in a minute.",
                    60 // 1 minute
            );
        }

        // Authenticate
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // Get user
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        log.info("User logged in successfully: {}", user.getId());

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                UserResponse.from(user)
        );
    }

    /**
     * Refresh access token using refresh token.
     */
    @Transactional
    public AuthResponse refreshAccessToken(String refreshTokenString) {
        log.info("Refreshing access token");

        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(refreshTokenString);
        User user = refreshToken.getUser();

        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
        );
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        log.info("Access token refreshed for user: {}", user.getId());

        return new AuthResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                UserResponse.from(user)
        );
    }

    /**
     * Logout user (revoke refresh token).
     */
    @Transactional
    public void logout(String refreshTokenString) {
        log.info("User logout");
        refreshTokenService.revokeRefreshToken(refreshTokenString);
    }
}