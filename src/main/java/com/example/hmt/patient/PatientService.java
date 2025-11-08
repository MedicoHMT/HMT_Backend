package com.example.hmt.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    // Get all patients
    public List<PatientModel> getAllPatients() {
        return patientRepository.findAll();
    }

    // Get one patient by their ID
    public Optional<PatientModel> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    // Create a new patient
    public PatientModel createPatient(PatientModel patient) {
        return patientRepository.save(patient);
    }

    // Update an existing patient
    public PatientModel updatePatient(Long id, PatientModel patientDetails) {
        PatientModel patient = patientRepository.findById(id)
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
        PatientModel patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        patientRepository.delete(patient);
    }
}
