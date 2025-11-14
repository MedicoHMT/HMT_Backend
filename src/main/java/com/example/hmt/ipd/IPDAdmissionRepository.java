package com.example.hmt.ipd;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IPDAdmissionRepository extends JpaRepository<IPDAdmission, Long> {
    List<IPDAdmission> findByPatientId(Long patientId);
    List<IPDAdmission> findByDoctorId(Long doctorId);
    List<IPDAdmission> findByStatus(String status);
}
