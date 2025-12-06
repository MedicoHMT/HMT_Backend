package com.example.hmt.doctor;

import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.tenant.Hospital;
import com.example.hmt.department.model.Department;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;


@Entity
@Table(name = "doctors",
        indexes = {
                @Index(name = "idx_doctor_hospital", columnList = "hospital_id"),
                @Index(name = "idx_doctor_department", columnList = "department_id")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Long doctorId;

    /**
     * Link to the canonical User entry. Each doctor MUST have a corresponding user account.
     * One-to-one mapping with unique constraint to ensure 1 user <-> 1 doctor.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_doctor_user"),
            unique = true)
    private User user;

    /**
     * Hospital FK. Use ManyToOne to enforce referential integrity.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hospital_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_doctor_hospital"))
    private Hospital hospital;

    /**
     * This references your Department table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "fk_doctor_department"))
    private Department department;

    /**
     * Doctor-specific attributes
     */
    @Column(length = 150)
    private String specialization;

    private int consultation_fee;

    /**
     * Auditing fields
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;


    public Long getHospitalId() {
        return hospital != null ? hospital.getId() : null;
    }

    public java.util.UUID getUserId() {
        return user != null ? user.getId() : null;
    }
}
