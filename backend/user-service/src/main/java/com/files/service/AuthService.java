package com.files.service;

import com.files.dto.AuthResponse;
import com.files.dto.LoginRequest;
import com.files.dto.RegisterRequest;
import com.files.dto.UserResponse;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<AuthResponse> login(LoginRequest request);
    
    Mono<UserResponse> registerAdmin(RegisterRequest request);

    Mono<UserResponse> register(RegisterRequest request);
}
