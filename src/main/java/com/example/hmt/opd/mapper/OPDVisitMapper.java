package com.example.hmt.opd.mapper;

import com.example.hmt.department.DepartmentMapper;
import com.example.hmt.doctor.DoctorMapper;
import com.example.hmt.opd.dto.OPDVisitResponseDTO;
import com.example.hmt.opd.model.OPDVisit;
import com.example.hmt.patient.PatientMapper;

public class OPDVisitMapper {
    public static OPDVisitResponseDTO mapToOPDVisitResponseDTO(OPDVisit visit, boolean deletedNotRequired) {
        if (visit == null) return null;
        if (visit.isDeleted() && deletedNotRequired) return null;

        return OPDVisitResponseDTO.builder()
                .opdId(visit.getOpdVisitId())
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
                .build();
    }
}
