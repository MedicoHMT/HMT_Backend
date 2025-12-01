package com.example.hmt.core.tenant.dto;

import com.example.hmt.core.tenant.Hospital;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HospitalDTO {
    // Add validations
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Code is required")
    private String hospitalCode;

    private String address;

    // Helper: Convert THIS DTO to an Entity
    public Hospital toEntity() {
        return Hospital.builder()
                .name(this.name)
                .hospitalCode(this.hospitalCode)
                .address(this.address)
                .build();
    }

    // Helper: Create a DTO from an Entity
    public static HospitalDTO fromEntity(Hospital h) {
        HospitalDTO dto = new HospitalDTO();
        dto.setName(h.getName());
        dto.setHospitalCode(h.getHospitalCode());
        dto.setAddress(h.getAddress());
        return dto;
    }
}

