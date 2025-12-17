package com.example.hmt.opd.repository;

import com.example.hmt.opd.model.OPDDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OPDDiagnosisRepository extends JpaRepository<OPDDiagnosis, Long> {
    Optional<OPDDiagnosis> findByOpdVisit_OpdVisitIdAndHospital_Id(String opdVisitId, Long hospitalId);
}
