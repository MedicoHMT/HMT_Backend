package com.example.hmt.core.tenant;

import com.example.hmt.core.auth.model.User;
import jakarta.persistence.*;
import jakarta.persistence.ForeignKey;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.Instant;


/**
 * Mapped superclass that brings tenant (hospital) and audit fields to all entities that extend it.
 * Also defines a Hibernate filter `tenantFilter` that can be enabled per-session to enforce
 * SQL-level tenant isolation: condition -> hospital_id = :hospitalId
 */
@MappedSuperclass
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "hospitalId", type = Long.class))
@Filter(name = "tenantFilter", condition = "hospital_id = :hospitalId")
@EntityListeners(AuditEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", foreignKey = @ForeignKey(name = "fk_baseentity_hospital"))
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", foreignKey = @ForeignKey(name = "fk_baseentity_created_by"))
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", foreignKey = @ForeignKey(name = "fk_baseentity_updated_by"))
    private User updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    private boolean isDeleted = false;

    public Long getHospitalId() {
        return hospital != null ? hospital.getId() : null;
    }

}
