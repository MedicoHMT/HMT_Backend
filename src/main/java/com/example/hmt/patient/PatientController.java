package com.example.hmt.patient;

import com.example.hmt.patient.dto.PatientRequestDTO;
import com.example.hmt.patient.dto.PatientResponseDTO;
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
    public List<PatientResponseDTO> getAllPatients() {
        return patientService.getAllPatients();
    }

    // GET /api/v1/patients/1  (Get patient by UHID)
    @GetMapping("/{uhid}")
    public ResponseEntity<PatientResponseDTO> getPatientByUhid(@PathVariable String uhid) {
        return ResponseEntity.ok(patientService.getPatientByUhid(uhid));
    }

    // POST /api/v1/patients  (Create a new patient)
    @PostMapping
    public PatientResponseDTO createPatient(@Valid @RequestBody PatientRequestDTO patient) {
        return patientService.createPatient(patient);
    }

    // PUT /api/v1/patients/1  (Update patient by ID)
    @PutMapping("/{uhid}")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable String uhid, @Valid @RequestBody PatientRequestDTO patientDetails) {
        try {
            PatientResponseDTO updatedPatient = patientService.updatePatient(uhid, patientDetails);
            return ResponseEntity.ok(updatedPatient);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/v1/patients/1  (Delete patient by ID)
    @PostMapping("/delete/{uhid}")
    public ResponseEntity<Void> deletePatient(@PathVariable String uhid) {
        try {
            patientService.deletePatient(uhid);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
