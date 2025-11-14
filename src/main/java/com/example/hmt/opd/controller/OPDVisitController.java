package com.example.hmt.opd.controller;

import com.example.hmt.core.tenant.TenantContext;
import com.example.hmt.opd.dto.OPDVisitRequestDTO;
import com.example.hmt.opd.dto.OPDVisitResponseDTO;
import com.example.hmt.opd.service.OPDVisitService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/opd/visits")
@Tag(name = "OPD Visits")
public class OPDVisitController {

    private final OPDVisitService visitService;

    public OPDVisitController(OPDVisitService visitService) {
        this.visitService = visitService;
    }

    @PostMapping
    public ResponseEntity<OPDVisitResponseDTO> createVisit(
            @Valid @RequestBody OPDVisitRequestDTO dto
    ) {
        return ResponseEntity.ok(visitService.createVisit(dto));
    }

    @GetMapping()
    public ResponseEntity<List<OPDVisitResponseDTO>> getVisit() {
        Long hospitalId = TenantContext.getHospitalId();
        List<OPDVisitResponseDTO> visits = visitService.getAllVisit(hospitalId);
        return ResponseEntity.ok(visits);
    }
    @GetMapping("/{id}")
    public ResponseEntity<OPDVisitResponseDTO> getVisit(@PathVariable Long id) {
        Long hospitalId = TenantContext.getHospitalId();
        return visitService.getVisitById(id, hospitalId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
