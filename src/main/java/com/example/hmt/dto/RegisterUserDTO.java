package com.example.hmt.dto;

import com.example.hmt.entity.Role;
import lombok.Data;

@Data
public class RegisterUserDTO {
    private String username;
    private String password;
    private Role role;
}
