package com.example.hmt.opd.service;

import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.auth.repository.UserRepository;
import com.example.hmt.core.handler.exception.ResourceNotFoundException;
import com.example.hmt.opd.dto.request.OPDInvestigationRequestDTO;
import com.example.hmt.opd.dto.response.OPDInvestigationResponseDTO;
import com.example.hmt.opd.mapper.OPDInvestigationMapper;
import com.example.hmt.opd.model.OPDInvestigation;
import com.example.hmt.opd.model.OPDVisit;
import com.example.hmt.opd.repository.OPDInvestigationRepository;
import com.example.hmt.opd.repository.OPDVisitRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OPDInvestigationService {
    private final OPDInvestigationRepository investigationRepository;
    private final OPDVisitRepository visitRepository;
    private final UserRepository userRepository;

    public OPDInvestigationService(
            OPDInvestigationRepository investigationRepository,
            OPDVisitRepository visitRepository,
            UserRepository userRepository) {
        this.investigationRepository = investigationRepository;
        this.visitRepository = visitRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String createInvestigation(OPDInvestigationRequestDTO investigationDTO, Long hospitalId) {
        OPDVisit opdVisit = visitRepository.findByOpdVisitIdAndHospital_Id(
                        investigationDTO.getOpdVisitId(), hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("OPD Visit Not Found"));

        User user = userRepository.findByUsername(
                        SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        OPDInvestigation investigation = OPDInvestigation.builder()
                .opdVisit(opdVisit)
                .testName(investigationDTO.getTestName())
                .category(investigationDTO.getCategory())
                .isUrgent(investigationDTO.getIsUrgent())
                .status(investigationDTO.getStatus())
                .recordedAt(Instant.now())
                .recordedBy(user)
                .build();

        investigation.setOpdInvestigationId("Test-" + investigation.getRecordedAt().toEpochMilli());

        investigationRepository.save(investigation);

        return "OPD Investigation Saved Successfully";
    }

    public List<OPDInvestigationResponseDTO> getOPDInvestigation(String opdVisitId, Long hospitalId) {
        return investigationRepository.findByOpdVisit_OpdVisitIdAndHospital_Id(opdVisitId, hospitalId)
                .stream()
                .map(investigation -> OPDInvestigationMapper.mapToOPDInvestigationResponseDTO(investigation, true))
                .collect(Collectors.toList());
    }
}
