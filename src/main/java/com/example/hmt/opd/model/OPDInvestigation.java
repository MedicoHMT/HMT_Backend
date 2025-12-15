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
@Table(name = "opd_investigation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OPDInvestigation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "opd_investigation_id", unique = true)
    private String opdInvestigationId;

    @ManyToOne
    @JoinColumn(name = "opd_visit_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_opd_investigation_opd_visit"))
    private OPDVisit opdVisit;

    @Column(name = "testName")
    private String testName;

    @Column(name = "category")
    private String category;

    @Column(name = "isUrgent")
    private boolean isUrgent;

    @Column(name = "status")
    private String status;

    @Column(name = "recorded_at")
    private Instant recordedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by", foreignKey = @ForeignKey(name = "fk_opd_investigation_recorded_by"))
    private User recordedBy;
}
