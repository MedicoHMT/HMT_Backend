package com.example.hmt.opd.model;

import com.example.hmt.core.tenant.BaseEntity;
import com.example.hmt.department.model.Department;
import com.example.hmt.doctor.Doctor;
import com.example.hmt.patient.Patient;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@Table(name = "opd_visits")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OPDVisit extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "opd_visit_id", unique = true)
    private String opdVisitId;
    private String opdVisitType;         // eg. "General", "Emergency", "Follow-up"

    private Long tokenNumber;
    private String reason;
    private String triageLevel;

    @Enumerated(EnumType.STRING)
    private VisitStatus status = VisitStatus.ACTIVE;

    @Column(name = "visit_date")
    @FutureOrPresent
    private Instant opdVisitDate;

    @Column(name = "consultation_fee")
    private int consultationFee;

    // Patient relationship
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_opd_visit_patient"))
    private Patient patient;

    // Doctor relationship
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_opd_visit_doctor"))
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_opd_visit_department"))
    private Department department;


    public Long getHospitalId() {
        return super.getHospitalId();
    }

    public Long getPatientId() {
        return patient != null ? patient.getId() : null;
    }

    public Long getDoctorId() {
        return doctor != null ? doctor.getDoctorId() : null;
    }

    public Long getDepartmentId() {
        return department != null ? department.getDepartment_id() : null;
    }
}
