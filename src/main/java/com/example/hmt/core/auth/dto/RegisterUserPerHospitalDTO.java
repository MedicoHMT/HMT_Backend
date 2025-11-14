package com.example.hmt.core.auth.dto;

import com.example.hmt.core.auth.model.Role;
import lombok.Data;

@Data
public class RegisterUserPerHospitalDTO {
    private String username;
    private String password;
    private Role role;
}