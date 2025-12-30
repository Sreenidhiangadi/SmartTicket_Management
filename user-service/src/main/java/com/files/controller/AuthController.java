package com.files.controller;

import com.files.dto.*;
import com.files.exception.NotFoundException;
import com.files.repository.UserRepository;
import com.files.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public Mono<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
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
