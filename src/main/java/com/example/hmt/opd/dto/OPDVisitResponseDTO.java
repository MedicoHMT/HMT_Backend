package com.example.hmt.opd.dto;

import com.example.hmt.department.dto.DepartmentResponseDTO;
import com.example.hmt.doctor.DoctorResponseDTO;
import com.example.hmt.opd.model.VisitStatus;
import com.example.hmt.patient.dto.PatientResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;


@Data
@Builder
public class OPDVisitResponseDTO {
    private String opdId;
    private String opdType;

    private String referBy;
    private Long tokenNumber;
    private String reason;
    private String triageLevel;

    private Instant visitDate;
    private int consultationFee;
    private VisitStatus status;

    private PatientResponseDTO patient;
    private DoctorResponseDTO doctor;
    private DepartmentResponseDTO department;
}