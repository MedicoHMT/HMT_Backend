package com.example.hmt.opd.mapper;

import com.example.hmt.department.DepartmentMapper;
import com.example.hmt.doctor.DoctorMapper;
import com.example.hmt.opd.dto.response.OPDDetailedVisitResponseDTO;
import com.example.hmt.opd.model.*;
import com.example.hmt.patient.PatientMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OPDDetailedVisitMapper {
    public static OPDDetailedVisitResponseDTO mapToOPDDetailedVisitResponseDTO(
            OPDVisit visit,
            Optional<OPDVitals> vitals,
            Optional<OPDAssessment> assessment,
            Optional<OPDDiagnosis> diagnosis,
            List<OPDInvestigation> investigations,
            boolean deletedNotRequired) {
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
                .opdVital(
                        vitals.map(vital ->
                                        OPDVitalMapper.mapToOPDVitalResponseDTO(vital, false))
                                .orElse(null)
                )
                .opdAssessment(
                        assessment.map(assessment1 ->
                                        OPDAssessmentMapper.mapToOPDAssessmentResponseDTO(assessment1, false))
                                .orElse(null)
                )
                .opdDiagnosis(
                        diagnosis.map(diagnosis1 ->
                                        OPDDiagnosisMapper.mapToOPDDiagnosisResponseDTO(diagnosis1, false))
                                .orElse(null)
                )
                .opdInvestigations(
                        investigations.stream()
                                .map(investigation -> OPDInvestigationMapper.mapToOPDInvestigationResponseDTO(investigation, false))
                                .collect(Collectors.toList())
                )
                .build();
    }
}
