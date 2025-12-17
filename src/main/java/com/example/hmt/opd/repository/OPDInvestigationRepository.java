package com.example.hmt.opd.repository;

import com.example.hmt.opd.model.OPDInvestigation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OPDInvestigationRepository extends JpaRepository<OPDInvestigation, Long> {
    List<OPDInvestigation> findByOpdVisit_OpdVisitIdAndHospital_Id(String opdVisitId, Long hospitalId);
}
