package com.files.controller;

import com.files.dto.*;
import com.files.exception.NotFoundException;
import com.files.repository.UserRepository;
import com.files.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public Mono<ResponseEntity<Map<String, Object>>> register(
            @Valid @RequestBody RegisterRequest request) {

        return authService.register(request)
            .map(user -> ResponseEntity.ok(
            		 Map.<String, Object>of(
            	                "message", "User registered successfully with id: " + user.getId()
            	            )
            ))
            .onErrorResume(IllegalArgumentException.class, ex ->
                Mono.just(
                    ResponseEntity.badRequest().body(
                        Map.<String, Object>of(
                            "message", ex.getMessage()
                        )
                    )
                )
            );
    }

    @PostMapping("/forgot-password")
    public Mono<Void> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {
        return authService.processForgotPassword(request.email());
    }

    @PostMapping("/reset-password")
    public Mono<Void> resetPassword(
            @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(
            request.token(),
            request.newPassword()
        );
    }


    @PostMapping("/register-admin")
    public Mono<UserResponse> registerAdmin(
            @Valid @RequestBody RegisterRequest request) {
        return authService.registerAdmin(request);
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public Mono<UserResponse> me(Authentication authentication) {

        JwtAuthenticationToken token =
                (JwtAuthenticationToken) authentication;

        String userId = token.getToken().getSubject();

        return userRepository.findById(userId)
                .switchIfEmpty(
                        Mono.error(new NotFoundException("User not found"))
                )
                .map(UserResponse::from);
    }
}
