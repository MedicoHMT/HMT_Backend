package com.example.hmt.opd.service;

import com.example.hmt.core.tenant.TenantContext;
import com.example.hmt.opd.dto.OPDAssessmentDTO;
import com.example.hmt.opd.model.OPDAssessment;
import com.example.hmt.opd.model.OPDVisit;
import com.example.hmt.opd.repository.OPDAssessmentRepository;
import com.example.hmt.opd.repository.OPDVisitRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OPDAssessmentService {

    private final OPDAssessmentRepository repo;
    private final OPDVisitRepository visitRepo;

    public OPDAssessmentService(OPDAssessmentRepository repo, OPDVisitRepository visitRepo) {
        this.repo = repo;
        this.visitRepo = visitRepo;
    }


    public OPDAssessment saveAssessment(OPDAssessmentDTO dto) {

        Long hospitalId = TenantContext.getHospitalId();

        OPDVisit visit = visitRepo.findByIdAndHospitalId(dto.getVisitId(), hospitalId)
                .orElseThrow(() -> new RuntimeException("OPD Visit not found"));

        OPDAssessment assessment = repo.findByVisitIdAndHospitalId(dto.getVisitId(), hospitalId)
                .orElse(new OPDAssessment());

        assessment.setHospitalId(hospitalId);
        assessment.setVisit(visit);

        assessment.setSymptoms(dto.getSymptoms());
        assessment.setGeneralExamination(dto.getGeneralExamination());
        assessment.setSystemicExamination(dto.getSystemicExamination());
        assessment.setProvisionalDiagnosis(dto.getProvisionalDiagnosis());
        assessment.setDietPlan(dto.getDietPlan());
        assessment.setNotes(dto.getNotes());

        return repo.save(assessment);
    }

    public Optional<OPDAssessment> getAssessment(Long visitId) {
        Long hospitalId = TenantContext.getHospitalId();
        return repo.findByVisitIdAndHospitalId(visitId, hospitalId);
    }

}
