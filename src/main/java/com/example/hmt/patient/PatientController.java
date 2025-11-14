package com.example.hmt.patient;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // GET /api/v1/patients  (Get all patients)
    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    // GET /api/v1/patients/1  (Get patient by ID)
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/v1/patients  (Create a new patient)
    @PostMapping
    public Patient createPatient(@Valid @RequestBody Patient patient) {
        return patientService.createPatient(patient);
    }

    // PUT /api/v1/patients/1  (Update patient by ID)
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @Valid @RequestBody Patient patientDetails) {
        try {
            Patient updatedPatient = patientService.updatePatient(id, patientDetails);
            return ResponseEntity.ok(updatedPatient);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/v1/patients/1  (Delete patient by ID)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        try {
            patientService.deletePatient(id);
            return ResponseEntity.noContent().build(); // Standard for successful delete
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
