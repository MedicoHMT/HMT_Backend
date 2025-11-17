package com.example.hmt.opd.dto;


import lombok.Data;

@Data
public class OPDVisitRequestDTO {
    private String patientUHId;
    private Long doctorId;
    private Double consultationFee;
    private String opdType;
}
