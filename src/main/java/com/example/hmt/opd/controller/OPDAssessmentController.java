package com.example.hmt.opd.controller;

import com.example.hmt.core.tenant.TenantContext;
import com.example.hmt.opd.dto.request.OPDAssessmentRequestDTO;
import com.example.hmt.opd.dto.response.OPDAssessmentResponseDTO;
import com.example.hmt.opd.service.OPDAssessmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/opd/assessment")
@Tag(name = "OPD Assessment")
public class OPDAssessmentController {
    private final OPDAssessmentService assessmentService;

    public OPDAssessmentController(OPDAssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping
    public ResponseEntity<String> create(
            @Valid @RequestBody OPDAssessmentRequestDTO dto) {
        Long hospitalId = TenantContext.getHospitalId();
        return ResponseEntity.ok(assessmentService.createAssessment(dto, hospitalId));
    }

    @GetMapping("/{opdVisitId}")
    public ResponseEntity<OPDAssessmentResponseDTO> getAssessmentByOpdVisitId(
            @PathVariable("opdVisitId") String opdVisitId) {
        Long hospitalId = TenantContext.getHospitalId();
        return ResponseEntity.ok(assessmentService.getOPDAssessment(opdVisitId, hospitalId));
    }
}
