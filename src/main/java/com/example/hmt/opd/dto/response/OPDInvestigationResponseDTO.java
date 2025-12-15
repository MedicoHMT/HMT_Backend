package com.example.hmt.opd.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OPDInvestigationResponseDTO {
    //private String opdVisitId;
    private String opdInvestigationId;
    private String testName;
    private String category;
    private Boolean isUrgent;
    private String status;
}
