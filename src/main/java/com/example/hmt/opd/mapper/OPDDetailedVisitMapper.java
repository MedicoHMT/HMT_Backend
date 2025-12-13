package com.example.hmt.opd.mapper;

import com.example.hmt.department.DepartmentMapper;
import com.example.hmt.doctor.DoctorMapper;
import com.example.hmt.opd.dto.OPDVisitResponseDTO;
import com.example.hmt.opd.dto.response.OPDDetailedVisitResponseDTO;
import com.example.hmt.opd.model.OPDAssessment;
import com.example.hmt.opd.model.OPDVisit;
import com.example.hmt.opd.model.OPDVitals;
import com.example.hmt.patient.PatientMapper;

public class OPDDetailedVisitMapper {
    public static OPDDetailedVisitResponseDTO mapToOPDDetailedVisitResponseDTO(OPDVisit visit, OPDVitals vitals, OPDAssessment assessment, boolean deletedNotRequired) {
        if (visit == null) return null;
        if (visit.isDeleted() && deletedNotRequired) return null;

        return OPDDetailedVisitResponseDTO.builder()
                .opdVisitId(visit.getOpdVisitId())
                .opdType(visit.getOpdVisitType())
                .tokenNumber(visit.getTokenNumber())
                .reason(visit.getReason())
                .triageLevel(visit.getTriageLevel())
                .visitDate(visit.getOpdVisitDate())
                .consultationFee(visit.getConsultationFee())
                .status(visit.getStatus())
                .patient(PatientMapper.toPatientResponseDto(visit.getPatient(), false))
                .doctor(DoctorMapper.toDoctorResponseDto(visit.getDoctor()))
                .department(DepartmentMapper.toDepartmentRequestDTO(visit.getDepartment()))
                .opdVital(OPDVitalMapper.mapToOPDVitalResponseDTO(vitals, false))
                .opdAssessment(OPDAssessmentMapper.mapToOPDAssessmentResponseDTO(assessment, false))
                .build();
    }
}
