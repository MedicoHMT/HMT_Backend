package com.example.hmt.opd.controller;

import com.example.hmt.core.tenant.TenantContext;
import com.example.hmt.opd.dto.request.OPDDiagnosisRequestDTO;
import com.example.hmt.opd.dto.response.OPDDiagnosisResponseDTO;
import com.example.hmt.opd.service.OPDDiagnosisService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/opd/diagnosis")
@Tag(name = "OPD Diagnosis")
public class OPDDiagnosisController {
    private final OPDDiagnosisService diagnosisService;

    public OPDDiagnosisController(OPDDiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @PostMapping
    public ResponseEntity<String> create(
            @Valid @RequestBody OPDDiagnosisRequestDTO dto) {
        Long hospitalId = TenantContext.getHospitalId();
        return ResponseEntity.ok(diagnosisService.createDiagnosis(dto, hospitalId));
    }

    @GetMapping("/{opdVisitId}")
    public ResponseEntity<OPDDiagnosisResponseDTO> getDiagnosisByOpdVisitId(
            @PathVariable("opdVisitId") String opdVisitId) {
        Long hospitalId = TenantContext.getHospitalId();
        return ResponseEntity.ok(diagnosisService.getOPDDiagnosis(opdVisitId, hospitalId));
    }
}
