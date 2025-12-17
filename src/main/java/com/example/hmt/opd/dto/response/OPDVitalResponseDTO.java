package com.example.hmt.opd.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OPDVitalResponseDTO {
    //private String opdVisitId;
    private Short pulseRate;
    private Short bpSystolic;
    private Short bpDiastolic;
    private Double temperature;
    private Short spo2;
    private Short respirationRate;
    private Double weight;
    private Double height;
}
