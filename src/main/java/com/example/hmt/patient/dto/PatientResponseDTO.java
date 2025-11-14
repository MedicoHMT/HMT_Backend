package com.example.hmt.patient.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientResponseDTO {
    private String uhid;
    private String firstName;
    private String lastName;

    private LocalDate dateOfBirth;
    private String gender;

    private String contactNumber;
    private String address;
}
