package com.example.hmt.doctor;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;


    private String firstName;
    private String lastName;
    private String specialization;
    private String department;
    private String contactNumber;
}
