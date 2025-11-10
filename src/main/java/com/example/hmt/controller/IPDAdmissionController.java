package com.example.hmt.controller;

import com.example.hmt.entity.*;
import com.example.hmt.service.IPDAdmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ipd")
@RequiredArgsConstructor
@Tag(name = "IPD Management", description = "Endpoints for managing IPD admissions")
public class IPDAdmissionController {

    private final IPDAdmissionService ipdAdmissionService;

    @PostMapping("/admit")
    @Operation(summary = "Admit a patient (Walk-In or from OPD)")
    public ResponseEntity<IPDAdmission> admit(@RequestBody IPDAdmission admission) {
        return ResponseEntity.ok(ipdAdmissionService.admitPatient(admission));
    }

    @PatchMapping("/{id}/discharge")
    @Operation(summary = "Discharge a patient from IPD")
    public ResponseEntity<IPDAdmission> discharge(@PathVariable Long id,
                                                  @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(ipdAdmissionService.dischargePatient(id, notes));
    }

    @GetMapping
    @Operation(summary = "Get all IPD admissions")
    public ResponseEntity<List<IPDAdmission>> getAll() {
        return ResponseEntity.ok(ipdAdmissionService.getAllAdmissions());
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get IPD admissions by doctor")
    public ResponseEntity<List<IPDAdmission>> getByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(ipdAdmissionService.getAdmissionsByDoctor(doctorId));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get IPD admissions by patient")
    public ResponseEntity<List<IPDAdmission>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ipdAdmissionService.getAdmissionsByPatient(patientId));
    }
}
