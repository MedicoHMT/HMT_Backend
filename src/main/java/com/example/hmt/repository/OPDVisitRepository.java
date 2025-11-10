package com.example.hmt.repository;


import com.example.hmt.entity.OPDVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OPDVisitRepository extends JpaRepository<OPDVisit, Long> {
    List<OPDVisit> findByDoctorId(Long doctorId);
    List<OPDVisit> findByPatientId(Long patientId);
    List<OPDVisit> findByVisitDate(LocalDate date);
}
