package com.example.hmt.opd.model;

import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.tenant.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@Table(name = "opd_assessment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OPDAssessment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opd_assessment_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "opd_visit_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_opd_vital_opd_visit"))
    private OPDVisit opdVisit;

    @Column(name = "symptoms")
    private String symptoms;

    @Column(name = "general_examination")
    private String generalExamination;

    @Column(name = "systemic_examination")
    private String systemicExamination;

    @Column(name = "provisional_diagnosis")
    private String provisionalDiagnosis;

    @Column(name = "diet_plan")
    private String dietPlan;

    @Column(name = "notes")
    private String notes;

    @Column(name = "recorded_at")
    private Instant recordedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by", foreignKey = @ForeignKey(name = "fk_opd_vital_recorded_by"))
    private User recordedBy;
}
