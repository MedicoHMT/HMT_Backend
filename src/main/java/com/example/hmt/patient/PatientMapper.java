package com.example.hmt.patient;

import com.example.hmt.patient.dto.PatientResponseDTO;

public class PatientMapper {
    public static PatientResponseDTO toPatientResponseDto(Patient p) {
        if (p == null) return null;

        PatientResponseDTO d = new PatientResponseDTO();
        d.setUhid(p.getUhid());
        d.setFirstName(p.getFirstName());
        d.setLastName(p.getLastName());
        d.setDateOfBirth(p.getDateOfBirth());
        d.setGender(p.getGender());
        d.setEmail(p.getEmail());
        d.setContactNumber(p.getContactNumber());
        d.setAddress(p.getAddress());
        d.setPhotoURL(p.getPhotoURL());
        d.setEmergencyContactName(p.getEmergencyContactName());
        d.setEmergencyContactNumber(p.getEmergencyContactNumber());

        return d;
    }
}
