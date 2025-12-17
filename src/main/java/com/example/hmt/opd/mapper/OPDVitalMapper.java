package com.example.hmt.opd.mapper;

import com.example.hmt.opd.dto.response.OPDVitalResponseDTO;
import com.example.hmt.opd.model.OPDVitals;

public class OPDVitalMapper {
    public static OPDVitalResponseDTO mapToOPDVitalResponseDTO(OPDVitals vitals, boolean deletedNotRequired) {
        if (vitals == null) return null;
        if (vitals.isDeleted() && deletedNotRequired) return null;
        return OPDVitalResponseDTO.builder()
                .pulseRate(vitals.getPulseRate())
                .bpSystolic(vitals.getBpSystolic())
                .bpDiastolic(vitals.getBpDiastolic())
                .temperature(vitals.getTemperature())
                .spo2(vitals.getSpo2())
                .respirationRate(vitals.getRespirationRate())
                .weight(vitals.getWeight())
                .height(vitals.getHeight())
                .build();
    }
}
