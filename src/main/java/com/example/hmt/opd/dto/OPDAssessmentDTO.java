package com.example.hmt.opd.dto;

import lombok.Data;

@Data
public class OPDAssessmentDTO {

    private String opd_id;

    private String symptoms;
    private String generalExamination;
    private String systemicExamination;
    private String provisionalDiagnosis;
    private String dietPlan;
    private String notes;
}
