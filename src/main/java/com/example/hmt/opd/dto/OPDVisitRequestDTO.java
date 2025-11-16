package com.example.hmt.opd.dto;


import lombok.Data;

@Data
public class OPDVisitRequestDTO {
    private Long patientId;
    private Long doctorId;
    private Double consultationFee;
    private String opdType;
}
