package com.example.hmt.patient;

import com.example.hmt.core.handler.exception.ResourceNotFoundException;
import com.example.hmt.core.tenant.*;
import com.example.hmt.patient.dto.PatientRequestDTO;
import com.example.hmt.patient.dto.PatientResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    // Get all patients
    public List<PatientResponseDTO> getAllPatients() {
        Long hospitalId = TenantContext.getHospitalId();
        if (hospitalId == null) {
            throw new IllegalArgumentException("Invalid hospital id");
        }
        return patientRepository
                .findAllByHospital_Id(hospitalId)
                .stream()
                .filter(p -> !p.isDeleted())
                .map(p -> PatientMapper.toPatientResponseDto(p, true))
                .collect(Collectors.toList());
    }

    // Get one patient by their ID
    public PatientResponseDTO getPatientByUhid(String id) {
        return patientRepository.findByUhid(id)
                .map(p -> PatientMapper.toPatientResponseDto(p, true))
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with UHID: " + id));
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

        Name patientName = Name.builder()
                .firstName(patient.getFirstName())
                .middleName(patient.getMiddleName())
                .lastName(patient.getLastName())
                .build();

        Address patientAddress = Address.builder()
                .street(patient.getStreet())
                .city(patient.getCity())
                .state(patient.getState())
                .country(patient.getCountry())
                .pinCode(patient.getPinCode())
                .build();

        Patient newPatient = Patient.builder()
                .name(patientName)
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .email(patient.getEmail())
                .contactNumber(patient.getContactNumber())
                .address(patientAddress)
                .photoURL(patient.getPhotoURL())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactNumber(patient.getEmergencyContactNumber())
                .build();

        Patient saved = patientRepository.save(newPatient);

        String newUhid = String.format("U%s%d", hospital.getHospitalCode(), saved.getId());
        newPatient.setUhid(newUhid);

        // Save and return the created Patient DTO (without id/hospitalId)
        saved = patientRepository.save(newPatient);
        return PatientMapper.toPatientResponseDto(saved, true);
    }

    // Update an existing patient
    public PatientResponseDTO updatePatient(String uhid, PatientRequestDTO dto) {

        Patient patient = patientRepository.findByUhid(uhid)
                .orElseThrow(() -> new RuntimeException("Patient not found with uhid: " + uhid));

        updatePatientFields(patient, dto);
        patient.setUpdatedAt(Instant.now());

        Patient updated = patientRepository.save(patient);
        return PatientMapper.toPatientResponseDto(updated, true);
    }

    // Delete a patient
    public void deletePatient(String uhid) {
        Patient patient = patientRepository.findByUhid(uhid)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + uhid));

        patient.setDeleted(true);
        patient.setUpdatedAt(Instant.now());

        patientRepository.save(patient);
    }

    private void updatePatientFields(Patient patient, PatientRequestDTO dto) {

        if (dto.getFirstName() != null) patient.getName().setFirstName(dto.getFirstName());
        if (dto.getMiddleName() != null) patient.getName().setMiddleName(dto.getMiddleName());
        if (dto.getLastName() != null) patient.getName().setLastName(dto.getLastName());

        if (dto.getDateOfBirth() != null) patient.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getGender() != null) patient.setGender(dto.getGender());
        if (dto.getEmail() != null) patient.setEmail(dto.getEmail());
        if (dto.getContactNumber() != null) patient.setContactNumber(dto.getContactNumber());

        if (dto.getStreet() != null) patient.getAddress().setStreet(dto.getStreet());
        if (dto.getCity() != null) patient.getAddress().setCity(dto.getCity());
        if (dto.getState() != null) patient.getAddress().setState(dto.getState());
        if (dto.getCountry() != null) patient.getAddress().setCountry(dto.getCountry());
        if (dto.getPinCode() != null) patient.getAddress().setPinCode(dto.getPinCode());

        if (dto.getPhotoURL() != null) patient.setPhotoURL(dto.getPhotoURL());
        if (dto.getEmergencyContactName() != null) patient.setEmergencyContactName(dto.getEmergencyContactName());
        if (dto.getEmergencyContactNumber() != null) patient.setEmergencyContactNumber(dto.getEmergencyContactNumber());
    }
}
