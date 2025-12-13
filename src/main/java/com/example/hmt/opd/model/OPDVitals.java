package com.example.hmt.opd.model;

import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.tenant.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@Table(name = "opd_vitals")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OPDVitals extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opd_vitals_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "opd_visit_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_opd_vital_opd_visit"))
    private OPDVisit opdVisit;

    @Column(name = "pulse")
    @Min(0)
    @Max(300)
    private Short pulseRate;

    @Column(name = "bp_systolic")
    @Min(0)
    @Max(300)
    private Short bpSystolic;

    @Column(name = "bp_diastolic")
    @Min(0)
    @Max(300)
    private Short bpDiastolic;

    @Column(name = "temperature")
    @Min(0)
    @Max(200)
    private Double temperature;

    @Column(name = "spo2")
    @Min(0)
    @Max(100)
    private Short spo2;

    @Column(name = "respiration_rate")
    @Min(0)
    @Max(100)
    private Short respirationRate;

    @Column(name = "weight")
    @Min(0)
    @Max(500)
    private Double weight;

    @Column(name = "height")
    @Min(0)
    @Max(500)
    private Double height;

    @Column(name = "recorded_at")
    private Instant recordedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by", foreignKey = @ForeignKey(name = "fk_opd_vital_recorded_by"))
    private User recordedBy;

    public Long getHospitalId() {
        return super.getHospitalId();
    }
}
