package com.example.hmt.opd.controller;

import com.example.hmt.core.tenant.TenantContext;
import com.example.hmt.opd.dto.request.OPDInvestigationRequestDTO;
import com.example.hmt.opd.dto.response.OPDInvestigationResponseDTO;
import com.example.hmt.opd.service.OPDInvestigationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/opd/investigations")
@Tag(name = "OPD Investigations")
public class OPDInvestigationController {

    private final OPDInvestigationService investigationService;

    public OPDInvestigationController(OPDInvestigationService investigationService) {
        this.investigationService = investigationService;
    }

    @PostMapping
    public ResponseEntity<String> create(
            @Valid @RequestBody OPDInvestigationRequestDTO dto) {
        Long hospitalId = TenantContext.getHospitalId();
        return ResponseEntity.ok(
                investigationService.createInvestigation(dto, hospitalId));
    }

    @GetMapping("/{opdVisitId}")
    public ResponseEntity<List<OPDInvestigationResponseDTO>> getInvestigationsByOpdVisit(
            @PathVariable("opdVisitId") String opdVisitId) {
        Long hospitalId = TenantContext.getHospitalId();
        return ResponseEntity.ok(investigationService.getOPDInvestigation(opdVisitId, hospitalId));
    }
}
