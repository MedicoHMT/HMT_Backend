package com.example.hmt.opd.model;

import com.example.hmt.core.tenant.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "opd_diagnosis")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OPDDiagnosis extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opd_diagnosis_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "opd_visit_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_opd_diagnosis_opd_visit"))
    private OPDVisit opdVisit;

    @Column(name = "icd10_code")
    private String icd10Code;

    @Column(name = "description")
    private String description;
}
