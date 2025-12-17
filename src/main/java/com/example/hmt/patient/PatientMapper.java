package com.example.hmt.patient;

import com.example.hmt.patient.dto.PatientResponseDTO;

public class PatientMapper {
    public static PatientResponseDTO toPatientResponseDto(Patient p, Boolean deletedNotRequired) {
        if (p == null) return null;
        if(p.isDeleted() && deletedNotRequired) return null;

        PatientResponseDTO d = new PatientResponseDTO();
        d.setUhid(p.getUhid());
        d.setFirstName(p.getName().getFirstName());
        d.setMiddleName(p.getName().getMiddleName());
        d.setLastName(p.getName().getLastName());
        d.setDateOfBirth(p.getDateOfBirth());
        d.setGender(p.getGender());
        d.setEmail(p.getEmail());
        d.setContactNumber(p.getContactNumber());
        d.setAddress(p.getAddress().getFullAddress());
        d.setPhotoURL(p.getPhotoURL());
        d.setEmergencyContactName(p.getEmergencyContactName());
        d.setEmergencyContactNumber(p.getEmergencyContactNumber());

        return d;
    }
}
