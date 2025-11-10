package com.example.hmt.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "ipd_admissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IPDAdmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A patient must exist
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Assigned doctor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Optional OPD reference if admitted from OPD
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opd_visit_id")
    private OPDVisit opdVisit;

    private LocalDate admissionDate;
    private LocalDate dischargeDate;

    private String roomNumber;
    private String bedNumber;
    private String diagnosis;
    private String treatmentNotes;

    @Enumerated(EnumType.STRING)
    private AdmissionType admissionType; // WALK_IN or OPD

    @Enumerated(EnumType.STRING)
    private AdmissionStatus status; // ADMITTED, DISCHARGED, CANCELLED
}
