package com.example.hmt.core.auth.dto;

import lombok.Data;

@Data
public class RegisterDoctorPerHospitalDTO {
    private String username;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private Long phoneNumber;
    private int consultation_fee;
    private Long department_id;
    private String specialization;
}
