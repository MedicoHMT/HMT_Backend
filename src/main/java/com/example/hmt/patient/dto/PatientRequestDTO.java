package com.example.hmt.patient.dto;

import com.example.hmt.core.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRequestDTO {
    private String firstName;
    private String middleName;
    private String lastName;

    private LocalDate dateOfBirth;
    private Gender gender;
    private String email;
    private String contactNumber;

    private String street;
    private String city;
    private String state;
    private String country;
    private String pinCode;

    private String photoURL;

    private String emergencyContactName;
    private String emergencyContactNumber;
}
