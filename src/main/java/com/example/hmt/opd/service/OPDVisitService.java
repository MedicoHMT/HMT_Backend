package com.example.hmt.opd.service;

import com.example.hmt.core.tenant.TenantContext;
import com.example.hmt.doctor.Doctor;
import com.example.hmt.doctor.DoctorRepository;
import com.example.hmt.opd.dto.OPDVisitRequestDTO;
import com.example.hmt.opd.dto.OPDVisitResponseDTO;
import com.example.hmt.opd.dto.OPDVisitStatusUpdateDTO;
import com.example.hmt.opd.model.OPDVisit;
import com.example.hmt.opd.model.VisitStatus;
import com.example.hmt.opd.repository.OPDVisitRepository;
import com.example.hmt.patient.Patient;
import com.example.hmt.patient.PatientRepository;
import com.example.hmt.patient.dto.PatientResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OPDVisitService {

    private final OPDVisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public OPDVisitService(OPDVisitRepository visitRepository,
                           PatientRepository patientRepository,
                           DoctorRepository doctorRepository) {
        this.visitRepository = visitRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }


    @Transactional
    public OPDVisitResponseDTO createVisit(OPDVisitRequestDTO dto) {

        Long hospitalId = TenantContext.getHospitalId();
        if (hospitalId == null)
            throw new IllegalArgumentException("Invalid hospital session");

        Patient patient = patientRepository.findPatientByUhid(dto.getPatientUHId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        OPDVisit visit = new OPDVisit();
        visit.setHospitalId(hospitalId);
        visit.setPatient(patient);
        visit.setDoctor(doctor);

        visit.setOpdType(dto.getOpdType());
        visit.setConsultationFee(dto.getConsultationFee());
        visit.setVisitDate(LocalDate.now());
        visit.setVisitTime(LocalTime.now());

        // Save once to get ID
        visit = visitRepository.save(visit);

        // Generate OPD ID (formatted)
        String opdId = "OPD-" + visit.getVisitDate().getYear() + "-" + String.format("%06d", visit.getId());
        visit.setOpdId(opdId);

        // Update record with OPD ID
        visit = visitRepository.save(visit);

        return mapToDTO(visit);
    }

    public Optional<OPDVisitResponseDTO> getVisitOPDById(String OPDid, Long hospitalId) {
        return visitRepository.findByOpdIdAndHospitalId(OPDid, hospitalId).map(this::mapToDTO);
    }
    public List<OPDVisitResponseDTO> getAllVisit(Long hospitalId) {
        List<OPDVisit> visits = visitRepository.findAllByHospitalId(hospitalId);
        return visits.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OPDVisitResponseDTO updateStatus(String visitId, OPDVisitStatusUpdateDTO dto) {

        Long hospitalId = TenantContext.getHospitalId();

        OPDVisit visit = visitRepository.findByOpdIdAndHospitalId(visitId, hospitalId)
                .orElseThrow(() -> new RuntimeException("OPD Visit not found"));

        VisitStatus newStatus;
        try {
            newStatus = VisitStatus.valueOf(dto.getStatus().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid status: " + dto.getStatus());
        }

        visit.setStatus(newStatus);

        visitRepository.save(visit);

        return mapToDTO(visit);
    }


    private OPDVisitResponseDTO mapToDTO(OPDVisit visit) {
        OPDVisitResponseDTO dto = new OPDVisitResponseDTO();

        dto.setOpdId(visit.getOpdId());
        dto.setOpdType(visit.getOpdType());
        dto.setPatientId(visit.getPatient().getId());
//        dto.setDoctorId(visit.getDoctor().getId());
        dto.setVisitDate(visit.getVisitDate());
        dto.setVisitTime(visit.getVisitTime());
        dto.setConsultationFee(visit.getConsultationFee());
        dto.setPatientUhid(visit.getPatient().getUhid());
        dto.setStatus(visit.getStatus().toString());

        // Map full patient details
        Patient p = visit.getPatient();
        PatientResponseDTO pdto = new PatientResponseDTO();
        pdto.setUhid(p.getUhid());
        pdto.setFirstName(p.getFirstName());
        pdto.setLastName(p.getLastName());
        pdto.setDateOfBirth(p.getDateOfBirth());
        pdto.setGender(p.getGender());
        pdto.setContactNumber(p.getContactNumber());
        pdto.setAddress(p.getAddress());
        dto.setPatient(pdto);

        // Map full doctor details
//        Doctor d = visit.getDoctor();
//        com.example.hmt.doctor.DoctorDTO ddto = new com.example.hmt.doctor.DoctorDTO();
//        ddto.setFirstName(d.getFirstName());
//        ddto.setLastName(d.getLastName());
//        ddto.setSpecialization(d.getSpecialization());
//        ddto.setContactNumber(d.getContactNumber());
//        ddto.setDepartment(d.getDepartment());
//        dto.setDoctor(ddto);

        return dto;
    }
}
