package com.example.hmt.opd.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OPDInvestigationRequestDTO {
    private String opdVisitId;
    private String testName;
    private String category;
    private Boolean isUrgent;
    private String status;
}
