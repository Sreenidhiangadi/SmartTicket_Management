package com.files.service.impl;

import com.files.dto.AuthResponse;
import com.files.dto.LoginRequest;
import com.files.dto.RegisterRequest;
import com.files.exception.BusinessException;
import com.files.model.Role;
import com.files.model.User;
import com.files.repository.UserRepository;
import com.files.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_success() {
        User user = User.builder()
                .email("test@test.com")
                .password("encoded")
                .active(true)
                .roles(Set.of(Role.USER))
                .build();

        LoginRequest req = new LoginRequest();
        req.setEmail("test@test.com");
        req.setPassword("pass");

        when(repo.findByEmail("test@test.com")).thenReturn(Mono.just(user));
        when(encoder.matches("pass", "encoded")).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("jwt");

        StepVerifier.create(authService.login(req))
                .expectNextMatches(r -> r instanceof AuthResponse)
                .verifyComplete();
    }

    @Test
    void login_invalidPassword() {
        User user = User.builder()
                .password("encoded")
                .active(true)
                .build();

        LoginRequest req = new LoginRequest();
        req.setEmail("test@test.com");
        req.setPassword("wrong");

        when(repo.findByEmail("test@test.com")).thenReturn(Mono.just(user));
        when(encoder.matches("wrong", "encoded")).thenReturn(false);

        StepVerifier.create(authService.login(req))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void login_inactiveUser() {
        User user = User.builder()
                .active(false)
                .build();

        LoginRequest req = new LoginRequest();
        req.setEmail("test@test.com");
        req.setPassword("pass");

        when(repo.findByEmail("test@test.com")).thenReturn(Mono.just(user));

        StepVerifier.create(authService.login(req))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void login_userNotFound() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@test.com");
        req.setPassword("pass");

        when(repo.findByEmail("test@test.com")).thenReturn(Mono.empty());

        StepVerifier.create(authService.login(req))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void register_success() {
        RegisterRequest req = new RegisterRequest();
        req.setName("User");
        req.setEmail("test@test.com");
        req.setPassword("pass");

        when(repo.existsByEmail("test@test.com")).thenReturn(Mono.just(false));
        when(encoder.encode("pass")).thenReturn("encoded");
        when(repo.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(authService.register(req))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void register_duplicateEmail() {
        RegisterRequest req = new RegisterRequest();
        req.setName("User");
        req.setEmail("test@test.com");
        req.setPassword("pass");

        when(repo.existsByEmail("test@test.com")).thenReturn(Mono.just(true));

        StepVerifier.create(authService.register(req))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void registerAdmin_success() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Admin");
        req.setEmail("admin@test.com");
        req.setPassword("pass");

        when(repo.existsByEmail("admin@test.com")).thenReturn(Mono.just(false));
        when(encoder.encode("pass")).thenReturn("encoded");
        when(repo.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(authService.registerAdmin(req))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void registerAdmin_duplicateEmail() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Admin");
        req.setEmail("admin@test.com");
        req.setPassword("pass");

        when(repo.existsByEmail("admin@test.com")).thenReturn(Mono.just(true));

        StepVerifier.create(authService.registerAdmin(req))
                .expectError(BusinessException.class)
                .verify();
    }
}
