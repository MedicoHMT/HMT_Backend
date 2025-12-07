package com.example.hmt.patient.dto;

import com.example.hmt.core.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientResponseDTO {
    private String uhid;
    private String firstName;
    private String lastName;

    private LocalDate dateOfBirth;
    private Gender gender;
    private String email;
    private String contactNumber;
    private String address;
    private String photoURL;

    private String emergencyContactName;
    private String emergencyContactNumber;
}
