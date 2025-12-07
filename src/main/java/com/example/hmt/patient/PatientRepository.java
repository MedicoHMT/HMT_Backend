package com.example.hmt.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient>findPatientByUhid(String Uhid);
    List<Patient> findAllByHospital_Id(Long hospitalId);
}
