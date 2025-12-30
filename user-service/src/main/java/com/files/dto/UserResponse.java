package com.files.dto;

import java.util.Set;

import com.files.model.Role;
import com.files.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String id;
    private String name;
    private String email;
    private Set<Role> roles;
    private boolean active;

    public static UserResponse from(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .roles(user.getRoles())
            .active(user.isActive())
            .build();
    }
}

