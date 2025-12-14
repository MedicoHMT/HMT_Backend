package com.example.hmt.opd.service;

import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.auth.repository.UserRepository;
import com.example.hmt.opd.dto.request.OPDDiagnosisRequestDTO;
import com.example.hmt.opd.dto.response.OPDDiagnosisResponseDTO;
import com.example.hmt.opd.mapper.OPDDiagnosisMapper;
import com.example.hmt.opd.model.OPDDiagnosis;
import com.example.hmt.opd.model.OPDVisit;
import com.example.hmt.opd.repository.OPDDiagnosisRepository;
import com.example.hmt.opd.repository.OPDVisitRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class OPDDiagnosisService {
    private final OPDDiagnosisRepository diagnosisRepository;
    private final OPDVisitRepository visitRepository;
    private final UserRepository userRepository;

    public OPDDiagnosisService(
            OPDDiagnosisRepository diagnosisRepository,
            OPDVisitRepository visitRepository,
            UserRepository userRepository) {
        this.diagnosisRepository = diagnosisRepository;
        this.visitRepository = visitRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String createDiagnosis(OPDDiagnosisRequestDTO diagnosisDTO, Long hospitalId) {
        OPDVisit opdVisit = visitRepository.findByOpdVisitIdAndHospital_Id(
                        diagnosisDTO.getOpdVisitId(), hospitalId)
                .orElseThrow(() -> new RuntimeException("OPD Visit Not Found"));
        User user = userRepository.findByUsername(
                        SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        OPDDiagnosis diagnosis = OPDDiagnosis.builder()
                .opdVisit(opdVisit)
                .icd10Code(diagnosisDTO.getIcd10Code())
                .description(diagnosisDTO.getDescription())
                .recordedAt(Instant.now())
                .recordedBy(user)
                .build();

        diagnosisRepository.save(diagnosis);

        return "OPD Diagnosis Saved Successfully";
    }

    public OPDDiagnosisResponseDTO getOPDDiagnosis(String opdVisitId, Long hospitalId) {
        return diagnosisRepository.findByOpdVisit_OpdVisitIdAndHospital_Id(opdVisitId, hospitalId)
                .map(diagnosis -> OPDDiagnosisMapper
                        .mapToOPDDiagnosisResponseDTO(diagnosis, true))
                .orElseThrow(() -> new RuntimeException("OPD Diagnosis Not Found"));
    }
}
