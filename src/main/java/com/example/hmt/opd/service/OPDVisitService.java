package com.example.hmt.opd.service;

import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.auth.repository.UserRepository;
import com.example.hmt.core.handler.exception.ResourceNotFoundException;
import com.example.hmt.core.tenant.TenantContext;
import com.example.hmt.department.model.Department;
import com.example.hmt.department.repository.DepartmentRepository;
import com.example.hmt.doctor.Doctor;
import com.example.hmt.doctor.DoctorRepository;
import com.example.hmt.opd.dto.OPDVisitRequestDTO;
import com.example.hmt.opd.dto.OPDVisitResponseDTO;
import com.example.hmt.opd.dto.OPDVisitStatusUpdateDTO;
import com.example.hmt.opd.mapper.OPDVisitMapper;
import com.example.hmt.opd.model.OPDVisit;
import com.example.hmt.opd.model.VisitStatus;
import com.example.hmt.opd.repository.OPDVisitRepository;
import com.example.hmt.patient.Patient;
import com.example.hmt.patient.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OPDVisitService {

    private final OPDVisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public OPDVisitService(OPDVisitRepository visitRepository,
                           PatientRepository patientRepository,
                           DoctorRepository doctorRepository,
                           DepartmentRepository departmentRepository,
                           UserRepository userRepository
    ) {
        this.visitRepository = visitRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public OPDVisitResponseDTO createVisit(OPDVisitRequestDTO dto) {

        Patient patient = patientRepository.findByUhid(dto.getPatientUHId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department no found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        OPDVisit visit = OPDVisit.builder()
                .opdVisitType(dto.getOpdType())
                .reason(dto.getReason())
                .triageLevel(dto.getTriageLevel())
                .opdVisitDate(Instant.now())
                .consultationFee(dto.getConsultationFee())
                .patient(patient)
                .doctor(doctor)
                .department(department)
                .build();

        visit = visitRepository.save(visit);

        // Generate OPD ID (formatted)
        String opdId = "OPD-" + visit.getOpdVisitDate().atZone(ZoneId.systemDefault()).getYear() + "-" + String.format("%06d", visit.getId());
        visit.setOpdVisitId(opdId);

        // Update record with OPD ID
        visit = visitRepository.save(visit);

        return OPDVisitMapper.mapToOPDVisitResponseDTO(visit, true);
    }

    public Optional<OPDVisitResponseDTO> getOPDVisitByOPDVisitId(String opdVisitId, Long hospitalId) {
        return visitRepository.findByOpdVisitIdAndHospital_Id(opdVisitId, hospitalId)
                .map(visit -> OPDVisitMapper.mapToOPDVisitResponseDTO(visit, true));
    }

    public List<OPDVisitResponseDTO> getAllVisit(Long hospitalId) {
        List<OPDVisit> visits = visitRepository.findAllByHospital_Id(hospitalId);
        return visits.stream()
                .map(visit -> OPDVisitMapper.mapToOPDVisitResponseDTO(visit, true))
                .collect(Collectors.toList());
    }

    public List<OPDVisitResponseDTO> getAllVisitByUhidAndHospitalId(String uhid, Long hospitalId) {
        Optional<Patient> patient = patientRepository.findByUhid(uhid);
        if (!patient.isPresent()) {
            throw new ResourceNotFoundException("Patient not found");
        }
        Long patientId = patient.get().getId();
        List<OPDVisit> visits = visitRepository.findAllByPatient_IdAndHospital_Id(patientId, hospitalId);
        return visits.stream()
                .map(visit -> OPDVisitMapper.mapToOPDVisitResponseDTO(visit, true))
                .collect(Collectors.toList());
    }

    @Transactional
    public OPDVisitResponseDTO updateStatus(String opdVisitId, OPDVisitStatusUpdateDTO dto) {

        Long hospitalId = TenantContext.getHospitalId();

        OPDVisit visit = visitRepository.findByOpdVisitIdAndHospital_Id(opdVisitId, hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("OPD Visit not found"));

        VisitStatus newStatus;
        try {
            newStatus = VisitStatus.valueOf(dto.getStatus().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid status: " + dto.getStatus());
        }

        visit.setStatus(newStatus);

        visitRepository.save(visit);

        return OPDVisitMapper.mapToOPDVisitResponseDTO(visit, true);
    }
}
