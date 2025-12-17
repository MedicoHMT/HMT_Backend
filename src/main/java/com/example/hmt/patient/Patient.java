package com.example.hmt.patient;

import com.example.hmt.core.enums.Gender;
import com.example.hmt.core.tenant.Address;
import com.example.hmt.core.tenant.BaseEntity;
import com.example.hmt.core.tenant.Name;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "patients")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing ID
    @Column(name = "patient_id")
    private Long id;

    // Public patient identifier (UHID)
    @Column(name = "uhid", unique = true)
    private String uhid;

    @Embedded
    private Name name;

    private LocalDate dateOfBirth;
    private Gender gender;
    private String email;

    @NotBlank(message = "Contact number cannot be blank")
    @Size(min = 10, max = 15, message = "Contact number must be between 10 and 15 digits")
    private String contactNumber;

    @Embedded
    private Address address;

    private String photoURL;

    private String emergencyContactName;
    private String emergencyContactNumber;


    public Long getHospitalId() {
        return super.getHospitalId();
    }
}
