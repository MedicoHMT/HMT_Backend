package com.example.hmt.opd.dto;

import com.example.hmt.patient.dto.PatientResponseDTO;
import com.example.hmt.doctor.DoctorDTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
public class OPDVisitResponseDTO {
    private String opdId;
    private String opdType;

    // keep legacy patientId for compatibility but prefer full patient object
    private Long patientId;
    private Long doctorId;

    private LocalDate visitDate;
    private LocalTime visitTime;
    private Double consultationFee;
    private String status;

    private String patientUhid;

    // New: full patient details
    private PatientResponseDTO patient;

    // New: full doctor details
    private DoctorDTO doctor;
}