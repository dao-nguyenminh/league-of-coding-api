package com.leagueofcoding.api.controller;

import com.leagueofcoding.api.dto.*;
import com.leagueofcoding.api.entity.User;
import com.leagueofcoding.api.exception.UserNotFoundException;
import com.leagueofcoding.api.repository.UserRepository;
import com.leagueofcoding.api.security.UserPrincipal;
import com.leagueofcoding.api.service.AuthService;
import com.leagueofcoding.api.util.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController - REST API endpoints cho authentication.
 *
 * @author dao-nguyenminh
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication APIs")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Operation(
            summary = "Register new user",
            description = "Create new account. Rate limit: 3 requests/hour per IP. " +
                    "Returns access token (15 min) and refresh token (7 days)."
    )
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "409", description = "Username or email already exists")
    @ApiResponse(responseCode = "429", description = "Rate limit exceeded (3 requests/hour)")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest
    ) {
        String clientIp = IpUtils.getClientIp(httpRequest);
        AuthResponse response = authService.register(request, clientIp);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Login user",
            description = "Authenticate user. Rate limit: 5 requests/minute per IP. " +
                    "Returns access token (15 min) and refresh token (7 days)."
    )
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @ApiResponse(responseCode = "429", description = "Rate limit exceeded (5 requests/minute)")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        String clientIp = IpUtils.getClientIp(httpRequest);
        AuthResponse response = authService.login(request, clientIp);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Refresh access token",
            description = "Get new access token using refresh token. Token rotation: old refresh token revoked, new one issued."
    )
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    @ApiResponse(responseCode = "401", description = "Invalid, expired, or revoked refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshAccessToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Logout user",
            description = "Revoke refresh token. Access token will remain valid until expiration (15 min)."
    )
    @ApiResponse(responseCode = "200", description = "Logout successful")
    @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get current user info",
            description = "Returns authenticated user information from JWT token.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "User info retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return ResponseEntity.ok(UserResponse.from(user));
    }
}