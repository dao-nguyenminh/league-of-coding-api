package com.leagueofcoding.api.service;

import com.leagueofcoding.api.dto.AuthResponse;
import com.leagueofcoding.api.dto.LoginRequest;
import com.leagueofcoding.api.dto.RegisterRequest;
import com.leagueofcoding.api.dto.UserResponse;
import com.leagueofcoding.api.entity.User;
import com.leagueofcoding.api.exception.EmailAlreadyExistsException;
import com.leagueofcoding.api.exception.UsernameAlreadyExistsException;
import com.leagueofcoding.api.repository.UserRepository;
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
    private final JWTokenProvider jwTokenProvider;

    /**
     * Register user mới.
     *
     * @param request RegisterRequest
     * @return AuthResponse chứa token và user info
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.username());

        // Check username uniqueness
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(
                    "Username '" + request.username() + "' is already taken"
            );
        }

        // Check email uniqueness
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

        // Generate token
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
        );
        String token = jwTokenProvider.generateToken(authentication);

        return new AuthResponse(token, UserResponse.from(user));
    }

    /**
     * Login user.
     *
     * @param request LoginRequest
     * @return AuthResponse chứa token và user info
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.username());

        // Authenticate credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // Generate token
        String token = jwTokenProvider.generateToken(authentication);

        // Get user info
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User logged in successfully: {}", user.getId());

        return new AuthResponse(token, UserResponse.from(user));
    }
}