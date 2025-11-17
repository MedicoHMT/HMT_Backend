package com.example.hmt.opd.controller;

import com.example.hmt.opd.dto.OPDVitalsDTO;
import com.example.hmt.opd.model.OPDVitals;
import com.example.hmt.opd.service.OPDVitalsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/opd/vitals")
@Tag(name = "OPD - Vitals")

public class OPDVitalsController {

    private final OPDVitalsService vitalsService;

    public OPDVitalsController(OPDVitalsService vitalsService) {
        this.vitalsService = vitalsService;
    }

    @PostMapping
    public ResponseEntity<OPDVitals> saveVitals(@RequestBody OPDVitalsDTO dto) {
        return ResponseEntity.ok(vitalsService.saveVitals(dto));
    }

    @GetMapping("/{visitId}")
    public ResponseEntity<OPDVitals> getVitals(@PathVariable String visitId) {
        return vitalsService.getVitals(visitId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
