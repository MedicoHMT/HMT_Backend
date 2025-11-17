package com.example.hmt.opd.repository;

import com.example.hmt.opd.model.OPDAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OPDAssessmentRepository extends JpaRepository<OPDAssessment, Long> {

    Optional<OPDAssessment> findByOpdVisitOpdIdAndHospitalId(String opdId, Long hospitalId);

}