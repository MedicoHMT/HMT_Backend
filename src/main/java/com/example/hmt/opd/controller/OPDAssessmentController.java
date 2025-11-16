package com.example.hmt.opd.controller;

import com.example.hmt.opd.dto.OPDAssessmentDTO;
import com.example.hmt.opd.model.OPDAssessment;
import com.example.hmt.opd.service.OPDAssessmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/opd/assessment")
@Tag(name = "OPD - Assessment")
public class OPDAssessmentController {

    private final OPDAssessmentService service;

    public OPDAssessmentController(OPDAssessmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OPDAssessment> save(@RequestBody OPDAssessmentDTO dto) {
        return ResponseEntity.ok(service.saveAssessment(dto));
    }

    @GetMapping("/{visitId}")
    public ResponseEntity<OPDAssessment> get(@PathVariable Long visitId) {
        return service.getAssessment(visitId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
