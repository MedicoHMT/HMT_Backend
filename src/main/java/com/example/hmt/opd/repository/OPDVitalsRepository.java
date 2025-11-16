package com.example.hmt.opd.repository;

import com.example.hmt.opd.model.OPDVitals;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OPDVitalsRepository extends JpaRepository<OPDVitals, Long> {


    Optional<OPDVitals> findByVisitIdAndHospitalId(Long visitId, Long hospitalId);
}