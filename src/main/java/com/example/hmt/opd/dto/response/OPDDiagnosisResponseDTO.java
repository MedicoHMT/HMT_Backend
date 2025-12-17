package com.example.hmt.opd.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OPDDiagnosisResponseDTO {
    //private String opdVisitId;
    private String icd10Code;
    private String description;
}
