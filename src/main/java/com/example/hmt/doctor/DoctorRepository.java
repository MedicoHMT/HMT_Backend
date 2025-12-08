package com.example.hmt.doctor;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
//    List<Doctor> findAllByHospital_Id(Long id);
}
