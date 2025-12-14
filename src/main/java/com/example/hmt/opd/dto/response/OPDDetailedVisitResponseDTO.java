package com.example.hmt.opd.dto.response;

import com.example.hmt.department.dto.DepartmentResponseDTO;
import com.example.hmt.doctor.DoctorResponseDTO;
import com.example.hmt.opd.model.VisitStatus;
import com.example.hmt.patient.dto.PatientResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class OPDDetailedVisitResponseDTO {
    private String opdVisitId;
    private String opdType;

    private Long tokenNumber;
    private String reason;
    private String triageLevel;

    private Instant visitDate;
    private int consultationFee;
    private VisitStatus status;

    private PatientResponseDTO patient;
    private DoctorResponseDTO doctor;
    private DepartmentResponseDTO department;

    private OPDVitalResponseDTO opdVital;
    private OPDAssessmentResponseDTO opdAssessment;
    private OPDDiagnosisResponseDTO opdDiagnosis;
}
