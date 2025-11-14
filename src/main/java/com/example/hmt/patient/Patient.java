package com.example.hmt.patient;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing ID
    private Long id;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    // Public patient identifier (UHID)
    @Column(name = "uhid", unique = true)
    private String uhid;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, message = "First name must be at least 2 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    private LocalDate dateOfBirth;
    private String gender;
    @NotBlank(message = "Contact number cannot be blank")
    @Size(min = 10, max = 15, message = "Contact number must be between 10 and 15 digits")
    private String contactNumber;
    private String address;
}
