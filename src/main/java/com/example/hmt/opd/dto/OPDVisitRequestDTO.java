package com.example.hmt.opd.dto;


import com.example.hmt.opd.model.VisitStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OPDVisitRequestDTO {
    private String patientUHId;
    private Long doctorId;
    private Long departmentId;
    private Long userId;

    private int consultationFee;
    private String opdType;
    private VisitStatus status;

    private String reason;
    private String triageLevel;
}
