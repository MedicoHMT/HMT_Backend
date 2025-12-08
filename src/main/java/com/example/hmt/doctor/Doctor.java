package com.example.hmt.doctor;

import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.tenant.BaseEntity;
import com.example.hmt.department.model.Department;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
public class Doctor extends BaseEntity {

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


    public Long getHospitalId() {
        return super.getHospitalId();
    }
}
