package com.example.hmt.patient;

import com.example.hmt.core.tenant.TenantContext;
import com.example.hmt.patient.dto.PatientRequestDTO;
import com.example.hmt.patient.dto.PatientResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    // Get all patients
    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    // Get one patient by their ID
    public Optional<PatientResponseDTO> getPatientById(String id) {
        return patientRepository.findPatientByUhid(id).map(this::toDto);
    }

    // Create a new patient
    @Transactional
    public PatientResponseDTO createPatient(PatientRequestDTO patient) {
        Patient newPatient = new Patient();
        newPatient.setFirstName(patient.getFirstName());
        newPatient.setLastName(patient.getLastName());
        newPatient.setDateOfBirth(patient.getDateOfBirth());
        newPatient.setGender(patient.getGender());
        newPatient.setContactNumber(patient.getContactNumber());
        newPatient.setAddress(patient.getAddress());

        Long hospitalId = TenantContext.getHospitalId();
        if (hospitalId != null) {
            newPatient.setHospitalId(hospitalId);
        }


        Patient saved = patientRepository.save(newPatient);

        String newUhid = String.format("U%d%d", LocalDate.now().getYear(), saved.getId());
        newPatient.setUhid(newUhid);

        // Save and return the created Patient DTO (without id/hospitalId)
        saved = patientRepository.save(newPatient);
        return toDto(saved);
    }

    // Update an existing patient
    public PatientResponseDTO updatePatient(Long id, PatientRequestDTO patientDetails) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        // Update the fields from DTO
        patient.setFirstName(patientDetails.getFirstName());
        patient.setLastName(patientDetails.getLastName());
        patient.setDateOfBirth(patientDetails.getDateOfBirth());
        patient.setGender(patientDetails.getGender());
        patient.setContactNumber(patientDetails.getContactNumber());
        patient.setAddress(patientDetails.getAddress());

        Patient updated = patientRepository.save(patient);
        return toDto(updated);
    }

    // Delete a patient
    public void deletePatient(String uhid) {
        Patient patient = patientRepository.findPatientByUhid(uhid)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + uhid));

        patientRepository.delete(patient);
    }

    private PatientResponseDTO toDto(Patient p) {
        PatientResponseDTO d = new PatientResponseDTO();
        d.setUhid(p.getUhid());
        d.setFirstName(p.getFirstName());
        d.setLastName(p.getLastName());
        d.setDateOfBirth(p.getDateOfBirth());
        d.setGender(p.getGender());
        d.setContactNumber(p.getContactNumber());
        d.setAddress(p.getAddress());
        return d;
    }
}
