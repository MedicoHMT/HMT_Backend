package com.example.hmt.opd.mapper;

import com.example.hmt.opd.dto.response.OPDInvestigationResponseDTO;
import com.example.hmt.opd.model.OPDInvestigation;

public class OPDInvestigationMapper {
    public static OPDInvestigationResponseDTO mapToOPDInvestigationResponseDTO(OPDInvestigation investigation, boolean deletedNotRequired) {
        if (investigation == null) return null;
        if (investigation.isDeleted() && deletedNotRequired) return null;

        return OPDInvestigationResponseDTO.builder()
                .opdInvestigationId(investigation.getOpdInvestigationId())
                .testName(investigation.getTestName())
                .category(investigation.getCategory())
                .isUrgent(investigation.isUrgent())
                .status(investigation.getStatus())
                .build();
    }
}
