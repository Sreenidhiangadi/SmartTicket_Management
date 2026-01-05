package com.files.service.impl;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.files.dto.AuthResponse;
import com.files.dto.LoginRequest;
import com.files.dto.RegisterRequest;
import com.files.dto.UserResponse;
import com.files.exception.BusinessException;
import com.files.model.Role;
import com.files.model.User;
import com.files.repository.UserRepository;
import com.files.service.AuthService;
import com.files.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    private static final Set<Role> PUBLIC_ROLES =
            Set.of(Role.USER, Role.AGENT, Role.MANAGER);

    @Override
    public Mono<AuthResponse> login(LoginRequest request) {

        return repo.findByEmail(request.getEmail())
            .filter(User::isActive)
            .filter(u -> encoder.matches(request.getPassword(), u.getPassword()))
            .switchIfEmpty(Mono.error(new BusinessException("Invalid credentials")))
            .map(user -> new AuthResponse(jwtUtil.generateToken(user)));
    }

    @Override
    public Mono<UserResponse> register(RegisterRequest request) {

        return repo.existsByEmail(request.getEmail())
            .flatMap(exists -> exists
                ? Mono.<UserResponse>error(
                    new BusinessException("Email already exists")
                  )
                : repo.save(
                    User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(encoder.encode(request.getPassword()))
                        .roles(Set.of(Role.USER))
                        .active(true)
                        .build()
                  ).map(u -> UserResponse.from(u))
            );
    }

    @Override
    public Mono<UserResponse> registerAdmin(RegisterRequest request) {
  return repo.existsByEmail(request.getEmail())
            .flatMap(exists -> exists
                ? Mono.error(new BusinessException("Email already exists"))
                : repo.save(
                    User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(encoder.encode(request.getPassword()))
                        .roles(Set.of(Role.ADMIN))
                        .active(true)
                        .build()
                  ).map(UserResponse::from)
            );
    }

}
