package com.example.hmt.opd.model;

import com.example.hmt.doctor.Doctor;
import com.example.hmt.patient.Patient;
import com.example.hmt.core.enums.VisitStatus;
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
