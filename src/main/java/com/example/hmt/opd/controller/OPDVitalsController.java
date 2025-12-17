package com.example.hmt.opd.controller;

import com.example.hmt.core.tenant.TenantContext;
import com.example.hmt.opd.dto.request.OPDVitalRequestDTO;
import com.example.hmt.opd.dto.response.OPDVitalResponseDTO;
import com.example.hmt.opd.service.OPDVitalsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/opd/vitals")
@Tag(name = "OPD Vitals")
public class OPDVitalsController {

    private final OPDVitalsService vitalsService;

    public OPDVitalsController(OPDVitalsService vitalsService) {
        this.vitalsService = vitalsService;
    }

    @PostMapping
    public ResponseEntity<String> create(
            @Valid @RequestBody OPDVitalRequestDTO dto) {
        Long hospitalId = TenantContext.getHospitalId();
        return ResponseEntity.ok(vitalsService.createVitals(dto, hospitalId));
    }

    @GetMapping("/{opdVisitId}")
    public ResponseEntity<OPDVitalResponseDTO> getVitalByOpdVisit(
            @PathVariable("opdVisitId") String opdVisitId) {
        Long hospitalId = TenantContext.getHospitalId();
        return ResponseEntity.ok(vitalsService.getOPDVitals(opdVisitId, hospitalId));
    }
}
