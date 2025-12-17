package com.example.hmt.opd.service;

import com.example.hmt.core.handler.exception.ResourceNotFoundException;
import com.example.hmt.opd.dto.request.OPDAssessmentRequestDTO;
import com.example.hmt.opd.dto.response.OPDAssessmentResponseDTO;
import com.example.hmt.opd.mapper.OPDAssessmentMapper;
import com.example.hmt.opd.model.OPDAssessment;
import com.example.hmt.opd.model.OPDVisit;
import com.example.hmt.opd.repository.OPDAssessmentRepository;
import com.example.hmt.opd.repository.OPDVisitRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OPDAssessmentService {
    private final OPDAssessmentRepository assessmentRepository;
    private final OPDVisitRepository visitRepository;

    public OPDAssessmentService(
            OPDAssessmentRepository assessmentRepository,
            OPDVisitRepository visitRepository) {
        this.assessmentRepository = assessmentRepository;
        this.visitRepository = visitRepository;
    }

    @Transactional
    public String createAssessment(OPDAssessmentRequestDTO assessmentDTO, Long hospitalId) {
        OPDVisit opdVisit = visitRepository.findByOpdVisitIdAndHospital_Id(
                        assessmentDTO.getOpdVisitId(), hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("OPD visit not found"));

        OPDAssessment assessment = OPDAssessment.builder()
                .opdVisit(opdVisit)
                .symptoms(assessmentDTO.getSymptoms())
                .generalExamination(assessmentDTO.getGeneralExamination())
                .systemicExamination(assessmentDTO.getSystemicExamination())
                .provisionalDiagnosis(assessmentDTO.getProvisionalDiagnosis())
                .dietPlan(assessmentDTO.getDietPlan())
                .notes(assessmentDTO.getNotes())
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
