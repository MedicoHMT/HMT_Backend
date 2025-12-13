package com.example.hmt.opd.service;

import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.auth.repository.UserRepository;
import com.example.hmt.core.handler.exception.ResourceNotFoundException;
import com.example.hmt.opd.dto.request.OPDAssessmentRequestDTO;
import com.example.hmt.opd.dto.response.OPDAssessmentResponseDTO;
import com.example.hmt.opd.mapper.OPDAssessmentMapper;
import com.example.hmt.opd.model.OPDAssessment;
import com.example.hmt.opd.model.OPDVisit;
import com.example.hmt.opd.repository.OPDAssessmentRepository;
import com.example.hmt.opd.repository.OPDVisitRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class OPDAssessmentService {
    private final OPDAssessmentRepository assessmentRepository;
    private final OPDVisitRepository visitRepository;
    private final UserRepository userRepository;

    public OPDAssessmentService(
            OPDAssessmentRepository assessmentRepository,
            OPDVisitRepository visitRepository,
            UserRepository userRepository) {
        this.assessmentRepository = assessmentRepository;
        this.visitRepository = visitRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String createAssessment(OPDAssessmentRequestDTO assessmentDTO, Long hospitalId) {
        OPDVisit opdVisit = visitRepository.findByOpdVisitIdAndHospital_Id(
                assessmentDTO.getOpdVisitId(), hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("OPD visit not found"));
        User user = userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not authenticated properly"));

        OPDAssessment assessment = OPDAssessment.builder()
                .opdVisit(opdVisit)
                .symptoms(assessmentDTO.getSymptoms())
                .generalExamination(assessmentDTO.getGeneralExamination())
                .systemicExamination(assessmentDTO.getSystemicExamination())
                .provisionalDiagnosis(assessmentDTO.getProvisionalDiagnosis())
                .dietPlan(assessmentDTO.getDietPlan())
                .notes(assessmentDTO.getNotes())
                .recordedAt(Instant.now())
                .recordedBy(user)
                .build();

        assessmentRepository.save(assessment);

        return "OPD Assessment Saved Successfully";
    }

    public OPDAssessmentResponseDTO getOPDAssessment(String opdVisitId, Long hospitalId) {
        return assessmentRepository.findByOpdVisit_OpdVisitIdAndHospital_Id(opdVisitId, hospitalId)
                .map(assessment -> OPDAssessmentMapper.mapToOPDAssessmentResponseDTO(assessment, true))
                .orElseThrow(() -> new ResourceNotFoundException("OPD Assessment Not Found"));
    }
}
