package com.example.hmt.core.auth.dto;

import com.example.hmt.core.auth.model.Role;
import lombok.Data;

@Data
public class RegisterUserPerHospitalDTO {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Long phoneNumber;
    private Role role;
}