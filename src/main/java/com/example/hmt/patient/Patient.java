package com.example.hmt.patient;

import com.example.hmt.core.enums.Gender;
import com.example.hmt.core.tenant.Hospital;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "patients")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing ID
    @Column(name = "patient_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hospital_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_patient_hospital"),
            unique = true)
    private Hospital hospital;

    // Public patient identifier (UHID)
    @Column(name = "uhid", unique = true)
    private String uhid;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, message = "First name must be at least 2 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    private LocalDate dateOfBirth;
    private Gender gender;
    private String email;

    @NotBlank(message = "Contact number cannot be blank")
    @Size(min = 10, max = 15, message = "Contact number must be between 10 and 15 digits")
    private String contactNumber;

    private String address;
    private String photoURL;

    private String emergencyContactName;
    private String emergencyContactNumber;

//    private String allergySummary;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public Long getHospitalId() {
        return hospital != null ? hospital.getId() : null;
    }
}
