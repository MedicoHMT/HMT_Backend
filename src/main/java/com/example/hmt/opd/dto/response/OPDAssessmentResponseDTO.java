package com.example.hmt.opd.dto.response;

import com.example.hmt.core.auth.model.User;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class OPDAssessmentResponseDTO {
    //private String opdVisitId;
    private String symptoms;
    private String generalExamination;
    private String systemicExamination;
    private String provisionalDiagnosis;
    private String dietPlan;
    private String notes;
}
