package com.example.hmt.opd.mapper;

import com.example.hmt.opd.dto.response.OPDAssessmentResponseDTO;
import com.example.hmt.opd.model.OPDAssessment;
import com.example.hmt.opd.model.OPDVisit;

public class OPDAssessmentMapper {
    public static OPDAssessmentResponseDTO mapToOPDAssessmentResponseDTO(OPDAssessment assessment, boolean deletedNotRequired) {
        if(assessment == null) return null;
        if(assessment.isDeleted() && deletedNotRequired) return null;

        return OPDAssessmentResponseDTO.builder()
                .symptoms(assessment.getSymptoms())
                .generalExamination(assessment.getGeneralExamination())
                .systemicExamination(assessment.getSystemicExamination())
                .provisionalDiagnosis(assessment.getProvisionalDiagnosis())
                .dietPlan(assessment.getDietPlan())
                .notes(assessment.getNotes())
                .build();
    }
}
