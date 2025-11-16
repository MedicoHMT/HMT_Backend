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

    private String opdId;
    private String opdType;         // eg. "General", "Emergency", "Follow-up"

    @Enumerated(EnumType.STRING)
    private VisitStatus status = VisitStatus.ACTIVE;

    private LocalDate visitDate;
    private LocalTime visitTime;


    private Double consultationFee;

    // Added for multi-tenant support
    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    // Patient relationship
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Doctor relationship
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
}
