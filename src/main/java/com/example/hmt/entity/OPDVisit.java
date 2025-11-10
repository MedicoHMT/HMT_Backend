package com.example.hmt.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "opd_visits")
public class OPDVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    private LocalDate visitDate;

    private String reason;

    private String diagnosis;

    private Double consultationFee;

    private String prescription;

    @Enumerated(EnumType.STRING)
    private VisitStatus status = VisitStatus.ACTIVE;

}
