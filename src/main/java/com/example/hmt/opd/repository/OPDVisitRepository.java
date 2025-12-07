package com.example.hmt.opd.repository;

import com.example.hmt.opd.model.OPDVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OPDVisitRepository extends JpaRepository<OPDVisit, Long> {

    List<OPDVisit> findAllByHospital_Id(Long hospitalId);
    List<OPDVisit> findAllByPatient_IdAndHospital_Id(Long patientId, Long hospitalId);
    Optional<OPDVisit>findByOpdVisitIdAndHospital_Id(String  opdVisitId, Long hospitalId);
}