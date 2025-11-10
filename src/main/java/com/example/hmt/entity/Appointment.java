package com.example.hmt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Appointment date and time cannot be null")
    @Future(message = "Appointment date must be in the future")
    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;

    private String reason;

    // --- Relationship with Patient ---
    @NotNull(message = "Appointment must have a patient")
    @ManyToOne(fetch = FetchType.LAZY) // Many appointments to ONE patient
    @JoinColumn(name = "patient_id", nullable = false) // Creates a 'patient_id' column
    private Patient patient;

    // --- Relationship with Doctor ---
    @NotNull(message = "Appointment must have a doctor")
    @ManyToOne(fetch = FetchType.LAZY) // Many appointments to ONE doctor
    @JoinColumn(name = "doctor_id", nullable = false) // Creates a 'doctor_id' column
    private Doctor doctor;
}
