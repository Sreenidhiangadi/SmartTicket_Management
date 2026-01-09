package com.files.service.impl;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
	private final JavaMailSender mailSender;

	private static final Set<Role> PUBLIC_ROLES = Set.of(Role.USER, Role.AGENT, Role.MANAGER);

	@Override
	public Mono<AuthResponse> login(LoginRequest request) {

	    return repo.findByEmail(request.getEmail())
	        .switchIfEmpty(Mono.error(new BusinessException("User does not exist")))

	        .filter(User::isActive)
	        .switchIfEmpty(Mono.error(new BusinessException("User account is inactive")))

	        .filter(user -> encoder.matches(request.getPassword(), user.getPassword()))
	        .switchIfEmpty(Mono.error(new BusinessException("Incorrect password")))

	        .map(user -> new AuthResponse(jwtUtil.generateToken(user)));
	}


	@Override
	public Mono<UserResponse> register(RegisterRequest request) {

		return repo.existsByEmail(request.getEmail()).flatMap(exists -> exists
				? Mono.<UserResponse>error(new BusinessException("Email already exists"))
				: repo.save(User.builder().name(request.getName()).email(request.getEmail())
						.password(encoder.encode(request.getPassword())).roles(Set.of(Role.USER)).active(true).build())
						.map(u -> UserResponse.from(u)));
	}

	@Override
	public Mono<UserResponse> registerAdmin(RegisterRequest request) {
		return repo.existsByEmail(request.getEmail()).flatMap(exists -> exists
				? Mono.error(new BusinessException("Email already exists"))
				: repo.save(User.builder().name(request.getName()).email(request.getEmail())
						.password(encoder.encode(request.getPassword())).roles(Set.of(Role.ADMIN)).active(true).build())
						.map(UserResponse::from));
	}
	@Override
	public Mono<Void> processForgotPassword(String email) {

	    return repo.findByEmail(email)
	        .flatMap(user -> {

	            String token = UUID.randomUUID().toString();

	            user.setResetToken(token);
	            user.setResetTokenExpiry(
	                Instant.now().plusSeconds(15 * 60) 
	            );

	            return repo.save(user)
	                .doOnSuccess(saved -> sendResetEmail(
	                    saved.getEmail(),
	                    token
	                ));
	        })
	        .then();
	}
	@Override
	public Mono<Void> resetPassword(String token, String newPassword) {

	    return repo.findByResetToken(token)
	        .switchIfEmpty(
	            Mono.error(new BusinessException("Invalid or expired token"))
	        )
	        .flatMap(user -> {

	            if (user.getResetTokenExpiry().isBefore(Instant.now())) {
	                return Mono.error(
	                    new BusinessException("Reset token expired")
	                );
	            }

	            user.setPassword(encoder.encode(newPassword));
	            user.setResetToken(null);
	            user.setResetTokenExpiry(null);

	            return repo.save(user).then();
	        });
	}
	private void sendResetEmail(String toEmail, String token) {

	    String resetLink =
	        "http://localhost:4200/reset-password?token=" + token;

	    SimpleMailMessage message = new SimpleMailMessage();
	    message.setTo(toEmail);
	    message.setSubject("Reset your password");
	    message.setText(
	        "You requested a password reset.\n\n" +
	        "Click the link below to reset your password:\n" +
	        resetLink + "\n\n" +
	        "This link is valid for 15 minutes.\n\n" +
	        "If you did not request this, please ignore this email."
	    );

	    mailSender.send(message);
	}


}
