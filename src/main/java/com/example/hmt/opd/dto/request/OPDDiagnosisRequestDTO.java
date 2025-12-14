package com.example.hmt.opd.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OPDDiagnosisRequestDTO {
    private String opdVisitId;
    private String icd10Code;
    private String description;
}
