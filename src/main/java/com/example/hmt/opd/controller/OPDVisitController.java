package com.example.hmt.opd.controller;


import com.example.hmt.opd.model.OPDVisit;
import com.example.hmt.core.enums.VisitStatus;
import com.example.hmt.opd.service.OPDVisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opd")
@RequiredArgsConstructor
@Tag(name = "OPD Management", description = "Endpoints for managing OPD visits")
public class OPDVisitController {

    private final OPDVisitService opdVisitService;

    @PostMapping
    @Operation(summary = "Create a new OPD visit")
    public ResponseEntity<OPDVisit> createVisit(@RequestBody OPDVisit opdVisit) {
        return ResponseEntity.ok(opdVisitService.createVisit(opdVisit));
    }

    @GetMapping
    @Operation(summary = "Get all OPD visits")
    public ResponseEntity<List<OPDVisit>> getAllVisits() {
        return ResponseEntity.ok(opdVisitService.getAllVisits());
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get OPD visits for a doctor")
    public ResponseEntity<List<OPDVisit>> getByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(opdVisitService.getVisitsByDoctor(doctorId));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get OPD visits for a patient")
    public ResponseEntity<List<OPDVisit>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(opdVisitService.getVisitsByPatient(patientId));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update OPD visit status")
    public ResponseEntity<OPDVisit> updateStatus(@PathVariable Long id, @RequestParam VisitStatus status) {
        return ResponseEntity.ok(opdVisitService.updateStatus(id, status));
    }
}
