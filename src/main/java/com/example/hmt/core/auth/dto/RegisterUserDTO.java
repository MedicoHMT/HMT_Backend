package com.example.hmt.core.auth.dto;

import lombok.Data;

@Data
public class RegisterUserDTO {
    private String username;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private Long phoneNumber;
    private String hospitalName;
    private String hospitalCode;
    private String hospitalAddress;
}
