package com.example.hmt.opd.mapper;

import com.example.hmt.opd.dto.response.OPDDiagnosisResponseDTO;
import com.example.hmt.opd.model.OPDDiagnosis;

public class OPDDiagnosisMapper {
    public static OPDDiagnosisResponseDTO mapToOPDDiagnosisResponseDTO(OPDDiagnosis diagnosis, boolean deletedNotRequired) {
        if (diagnosis == null) return null;
        if (diagnosis.isDeleted() && deletedNotRequired) return null;
        return OPDDiagnosisResponseDTO.builder()
                .icd10Code(diagnosis.getIcd10Code())
                .description(diagnosis.getDescription())
                .build();
    }
}
