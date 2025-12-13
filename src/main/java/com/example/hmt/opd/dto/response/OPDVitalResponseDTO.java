package com.example.hmt.opd.dto.response;

import com.example.hmt.core.auth.model.User;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

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
