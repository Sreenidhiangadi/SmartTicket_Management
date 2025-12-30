package com.files.controller;

import com.files.dto.*;
import com.files.exception.BusinessException;
import com.files.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/admin")
    public Flux<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/admin/{id}")
    public Mono<UserResponse> updateUser(
            @PathVariable String id,
            @RequestBody AdminUserUpdateRequest request) {

        if (request.getRoles() != null) {
            return userService.updateRoles(id, request.getRoles());
        }

        if (request.getActive() != null) {
            return userService.updateStatus(id, request.getActive());
        }

        return Mono.error(new BusinessException("No update fields provided"));
    }

    @GetMapping("/internal/{id}")
    public Mono<UserResponse> getUserById(@PathVariable String id) {
        return userService.getById(id);
    }
    @GetMapping("/agents")
    public Flux<UserResponse> getActiveAgents() {
        return userService.getActiveAgents();
    }

}
