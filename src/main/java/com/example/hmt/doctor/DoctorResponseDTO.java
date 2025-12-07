package com.example.hmt.doctor;

import com.example.hmt.department.dto.DepartmentResponseDTO;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class DoctorResponseDTO {

    private Long doctorId;

    private String firstName;
    private String lastName;

    private String specialization;
    private int consultationFee;

    private DepartmentResponseDTO department;
}