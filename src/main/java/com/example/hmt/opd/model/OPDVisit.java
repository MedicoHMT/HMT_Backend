package com.example.hmt.opd.model;

import com.example.hmt.doctor.Doctor;
import com.example.hmt.patient.Patient;
import com.example.hmt.core.enums.VisitStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "opd_visits")
public class OPDVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Added for multi-tenant support
    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;


    // OPD Registration / UHID
    @Column(nullable = false)
    private String uhid;

    // Patient relationship
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Doctor relationship
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    private LocalDate visitDate;
    private LocalTime visitTime;

    // Queue Token Number
    private Integer tokenNumber;

    private String opdNumber;

    @Enumerated(EnumType.STRING)
    private VisitStatus status = VisitStatus.ACTIVE;

    private Double consultationFee;

}
