package com.example.hmt.opd.dto;

import lombok.Data;

@Data
public class OPDVitalsDTO {
    private Long visitId;

    private Integer pulse;
    private Integer spo2;
    private String bp;
    private Double temperature;
    private Integer respiration;
    private Double weight;
    private Double height;

}
