package com.example.hmt.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Finds all appointments for a given patient's ID
    List<Appointment> findByPatientId(Long patientId);

    // Finds all appointments for a given doctor's ID
    List<Appointment> findByDoctorId(Long doctorId);

}
