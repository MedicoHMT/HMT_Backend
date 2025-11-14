package com.example.hmt.patient;

import com.example.hmt.core.tenant.TenantContext;
import com.example.hmt.patient.dto.PatientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    // Get all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // Get one patient by their ID
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    // Create a new patient
    public PatientDTO createPatient(PatientDTO patient) {
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

        // Save and return the created Patient entity
        return toPatientResponseDto(patientRepository.save(newPatient));
    }

    // Update an existing patient
    public Patient updatePatient(Long id, Patient patientDetails) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        // Update the fields
        patient.setFirstName(patientDetails.getFirstName());
        patient.setLastName(patientDetails.getLastName());
        patient.setDateOfBirth(patientDetails.getDateOfBirth());
        patient.setGender(patientDetails.getGender());
        patient.setContactNumber(patientDetails.getContactNumber());
        patient.setAddress(patientDetails.getAddress());

        return patientRepository.save(patient);
    }

    // Delete a patient
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        patientRepository.delete(patient);
    }

    private PatientDTO toPatientResponseDto(Patient p) {
        PatientDTO d = new PatientDTO();
        d.setFirstName(p.getFirstName());
        d.setLastName(p.getLastName());
        d.setDateOfBirth(p.getDateOfBirth());
        d.setGender(p.getGender());
        d.setContactNumber(p.getContactNumber());
        d.setAddress(p.getAddress());
        return d;
    }
}
