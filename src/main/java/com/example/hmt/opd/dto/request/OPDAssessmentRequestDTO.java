package com.example.hmt.opd.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class OPDAssessmentRequestDTO {
    private String opdVisitId;
    private String symptoms;
    private String generalExamination;
    private String systemicExamination;
    private String provisionalDiagnosis;
    private String dietPlan;
    private String notes;
}
