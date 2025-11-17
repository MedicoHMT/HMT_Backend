package com.example.hmt.opd.repository;

import com.example.hmt.opd.model.OPDVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OPDVisitRepository extends JpaRepository<OPDVisit, Long> {

    List<OPDVisit> findAllByHospitalId(Long hospitalId);
    Optional<OPDVisit>findByOpdIdAndHospitalId(String  opdId, Long hospitalId);
}