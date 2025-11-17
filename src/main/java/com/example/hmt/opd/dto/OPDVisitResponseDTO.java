package com.example.hmt.opd.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
public class OPDVisitResponseDTO {
    private String opdId;
    private String opdType;

    private Long patientId;
    private Long doctorId;

    private LocalDate visitDate;
    private LocalTime visitTime;
    private Double consultationFee;
    private String status;

    private String patientUhid;
}
