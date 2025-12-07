package com.example.hmt.patient;

import com.example.hmt.core.tenant.Hospital;
import com.example.hmt.core.tenant.HospitalRepository;
import com.example.hmt.core.tenant.TenantContext;
import com.example.hmt.patient.dto.PatientRequestDTO;
import com.example.hmt.patient.dto.PatientResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    // Get all patients
    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll().stream().map(PatientMapper::toPatientResponseDto).collect(Collectors.toList());
    }

    // Get one patient by their ID
    public Optional<PatientResponseDTO> getPatientByUhid(String id) {
        return patientRepository.findPatientByUhid(id).map(PatientMapper::toPatientResponseDto);
    }

    // Create a new patient
    @Transactional
    public PatientResponseDTO createPatient(PatientRequestDTO patient) {
        Long hospitalId = TenantContext.getHospitalId();
        if (hospitalId == null)
            throw new IllegalArgumentException("Invalid hospital session");
        Hospital hospital = hospitalRepository.findById(hospitalId).orElse(null);
        if (hospital == null)
            throw new IllegalArgumentException("Invalid hospital session");

        Patient newPatient = Patient.builder()
                .hospital(hospital)
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .email(patient.getEmail())
                .contactNumber(patient.getContactNumber())
                .address(patient.getAddress())
                .photoURL(patient.getPhotoURL())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactNumber(patient.getEmergencyContactNumber())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Patient saved = patientRepository.save(newPatient);

        String newUhid = String.format("U%d%d", LocalDate.now().getYear(), saved.getId());
        newPatient.setUhid(newUhid);
        newPatient.setCreatedAt(Instant.now());

        // Save and return the created Patient DTO (without id/hospitalId)
        saved = patientRepository.save(newPatient);
        return PatientMapper.toPatientResponseDto(saved);
    }

    // Update an existing patient
    public PatientResponseDTO updatePatient(String uhid, PatientRequestDTO patientDetails) {
        Patient patient = patientRepository.findPatientByUhid(uhid)
                .orElseThrow(() -> new RuntimeException("Patient not found with uhid: " + uhid));

        // Update the fields from DTO
        patient.setFirstName(patientDetails.getFirstName());
        patient.setLastName(patientDetails.getLastName());
        patient.setDateOfBirth(patientDetails.getDateOfBirth());
        patient.setGender(patientDetails.getGender());
        patient.setEmail(patientDetails.getEmail());
        patient.setContactNumber(patientDetails.getContactNumber());
        patient.setAddress(patientDetails.getAddress());
        patient.setPhotoURL(patientDetails.getPhotoURL());
        patient.setEmergencyContactName(patient.getEmergencyContactName());
        patient.setEmergencyContactNumber(patient.getEmergencyContactNumber());
        patient.setUpdatedAt(Instant.now());

        Patient updated = patientRepository.save(patient);
        return PatientMapper.toPatientResponseDto(updated);
    }

    // Delete a patient
    public void deletePatient(String uhid) {
        Patient patient = patientRepository.findPatientByUhid(uhid)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + uhid));

        patientRepository.delete(patient);
    }
}
