package com.example.hmt.opd.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "opd_assessment")
@Data
public class OPDAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "visit_id", nullable = false, unique = true)
    private OPDVisit visit;

    private String symptoms;

    @Column(columnDefinition = "TEXT")
    private String generalExamination;

    @Column(columnDefinition = "TEXT")
    private String systemicExamination;

    @Column(columnDefinition = "TEXT")
    private String provisionalDiagnosis;

    @Column(columnDefinition = "TEXT")
    private String dietPlan;

    @Column(columnDefinition = "TEXT")
    private String notes;



    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

}
