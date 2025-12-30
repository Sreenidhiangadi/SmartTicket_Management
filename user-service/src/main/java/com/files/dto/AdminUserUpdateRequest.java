package com.files.dto;

import java.util.Set;

import com.files.model.Role;

import lombok.Data;

@Data
public class AdminUserUpdateRequest {

    private Set<Role> roles;   
    private Boolean active;    
}
